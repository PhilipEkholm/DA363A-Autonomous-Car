"""
Rc_driver.py

The main server file for autonomous driving.

Variables:
    sensor_data: global variable that can be one way shared between threads
""" 

import threading
import SocketServer
import serial
import cv2
import numpy as np
import math
import socket

# distance data measured by ultrasonic sensor
sensor_data = " "


class NeuralNetwork(object):
    """
    NeuralNetwork
    
    class for setting up neural network with OpenCV 2.
    """

    def __init__(self):
        #Load model with MLP
        self.model = cv2.ANN_MLP()


    #Create the model and load the training file
    def create(self):
        layer_size = np.int32([38400, 32, 4])
        self.model.create(layer_size)
        self.model.load('mlp_xml/mlp.xml')

    #Give a prediction of upcoming images
    def predict(self, samples):
        ret, resp = self.model.predict(samples)
        return resp.argmax(-1)

"""
RCControl

This is the class responsible for sending data back to the raspberry pi about
what to do next. It uses a socket to send data over using a TCP-stream
"""

class RCControl(object):
    def __init__(self):
        TCP_IP = '169.254.230.248' #IP-address can vary
        TCP_PORT = 8001
        #Create a socket
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s.connect((TCP_IP, TCP_PORT))

    #Use the prediction from the neural net and send it over
    def steer(self, prediction):
        if prediction == 2:
            self.s.send('1')
            print("Forward")
        elif prediction == 0:
            self.s.send('7')
            print("Left")
        elif prediction == 1:
            self.s.send('6')
            print("Right")
        else:
            self.stop()

    def stop(self):
        self.s.send('0')

"""
DistanceToCamera

A helper class for determinating the distance to a detected sign
using monocular vision. The method depends on that the camera downward 
angle is correct. The lower the angle the more accurate the calculation
becomes.

Normal downward angle: 8 degrees (0,14 rad)
"""
class DistanceToCamera(object):

    def __init__(self):
        # camera params
        self.alpha = 8.0 * math.pi / 180
        #Camera values from the camera calibrations
        self.v0 = 119.865631204
        self.ay = 332.262498472

    def calculate(self, v, h, x_shift, image):
        # compute and return the distance from the target point to the camera
        d = h / math.tan(self.alpha + math.atan((v - self.v0) / self.ay))
        if d > 0:
            cv2.putText(image, "%.1fcm" % d,
                (image.shape[1] - x_shift, image.shape[0] - 20), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)
        return d

"""
ObjectDetection

The object detection contains code for detecting different classifiers
and draw identifiers on them to the camera feed. Some obselete code for
traffic light can still be found.
"""
class ObjectDetection(object):

    def __init__(self):
        self.red_light = False
        self.green_light = False
        self.yellow_light = False

    def detect(self, cascade_classifier, gray_image, image, sign):

        # y camera coordinate of the target point 'P'
        v = 0

        # minimum value to proceed traffic light state validation
        threshold = 150     
        
        # detection
        cascade_obj = cascade_classifier.detectMultiScale(
            gray_image,
            scaleFactor=1.1,
            minNeighbors=5,
            minSize=(30, 30),
            flags=cv2.cv.CV_HAAR_SCALE_IMAGE
        )

        # draw a rectangle around the objects
        for (x_pos, y_pos, width, height) in cascade_obj:
            cv2.rectangle(image, (x_pos+5, y_pos+5), (x_pos+width-5, y_pos+height-5), (255, 255, 255), 2)
            v = y_pos + height - 5
            #print(x_pos+5, y_pos+5, x_pos+width-5, y_pos+height-5, width, height)

            # stop sign
            if width/height == 1 and sign == 1:
                cv2.putText(image, 'STOP', (x_pos, y_pos-10), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
            
             # turn right sign
            elif width/height == 1 and sign == 2:
                cv2.putText(image, 'RIGHT', (x_pos, y_pos-10), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)

            # turn left sign
            elif width/height == 1 and sign == 3:
                cv2.putText(image, 'LEFT', (x_pos, y_pos-10), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 0, 0), 2)
            
        return v

"""
SensorDataHandler

Collect data from the ultrasonic sensor found on the car and round the float
value to one decimal, assign the global variable the value to be used from
the other thread.
"""
class SensorDataHandler(SocketServer.BaseRequestHandler):

    data = " "

    def handle(self):
        global sensor_data
        try:
            while self.data:
                self.data = self.request.recv(1024)
                sensor_data = round(float(self.data), 1)
                #Debug for viewing client address
                #print "{} sent:".format(self.client_address[0])
                print sensor_data
        finally:
            print "Connection closed on thread 2"

"""
VideoStreamHandler

Extends socketserver and is the main class in the program. 
The stream handler will collect jpegs from the camera and use it to determine
the next upcoming action. It will also take note of the ultrasonic sensor
as well as eventual stop signs.
"""
class VideoStreamHandler(SocketServer.StreamRequestHandler):

    # h1: stop sign
    h1 = 15.5 - 10  # cm
    h2 = 15.5 - 10

    # create neural network
    model = NeuralNetwork()
    model.create()

    obj_detection = ObjectDetection()
    rc_car = RCControl()

    # cascade classifiers
    stop_cascade = cv2.CascadeClassifier('cascade_xml/stop_sign.xml')
    right_cascade = cv2.CascadeClassifier('cascade_xml/righthaar.xml')
    left_cascade = cv2.CascadeClassifier('cascade_xml/lefthaar.xml')
    light_cascade = cv2.CascadeClassifier('cascade_xml/traffic_light.xml')

    #Initial values to signs. Any lower value than 25 will
    #trigger neural network.
    d_to_camera = DistanceToCamera()
    d_stop_sign = 25
    d_right_sign = 25
    d_left_sign = 25
    d_light = 25

    stop_start = 0              # start time when stop at the stop sign
    stop_finish = 0
    stop_time = 0
    drive_time_after_stop = 0

    def handle(self):

        global sensor_data
        stream_bytes = ' '
        stop_flag = False
        turn_left_flag = False
        turn_right_flag = False
        stop_sign_active = True

        # stream video frames one by one
        try:
            while True:
                stream_bytes += self.rfile.read(1024)
                first = stream_bytes.find('\xff\xd8')
                last = stream_bytes.find('\xff\xd9')
                #Check if feed as been sent
                if first != -1 and last != -1:
                    jpg = stream_bytes[first:last+2]
                    stream_bytes = stream_bytes[last+2:]
                    gray = cv2.imdecode(np.fromstring(jpg, dtype=np.uint8), cv2.CV_LOAD_IMAGE_GRAYSCALE)
                    image = cv2.imdecode(np.fromstring(jpg, dtype=np.uint8), cv2.CV_LOAD_IMAGE_UNCHANGED)

                    # only lower half of the image is used to determine the path for neural network.
                    half_gray = gray[120:240, :]

                    # object detection
                    v_param1 = self.obj_detection.detect(self.stop_cascade, gray, image, 1)
                    v_param2 = self.obj_detection.detect(self.right_cascade,gray, image, 2)
                    v_param3 = self.obj_detection.detect(self.left_cascade,gray, image, 3)

                    # distance measurement
                    if v_param1 > 0 or v_param2 > 0 or v_param3 > 0:
                        d1 = self.d_to_camera.calculate(v_param1, self.h1, 300, image)
                        d2 = self.d_to_camera.calculate(v_param2, self.h1, 300, image)
                        d3 = self.d_to_camera.calculate(v_param3, self.h1, 300, image)
                        self.d_stop_sign = d1
                        self.d_right_sign = d2
                        self.d_left_sign = d3

                    cv2.imshow('image', image)
                    #cv2.imshow('mlp_image', half_gray)

                    # reshape image
                    image_array = half_gray.reshape(1, 38400).astype(np.float32)
                    
                    # neural network makes prediction
                    prediction = self.model.predict(image_array)

                    # stop conditions for sensor data. The greater than 5 is to avoid
                    #arbitrary values since the sensor data is relying on actually hitting anything
                    if sensor_data is not None and 5 < sensor_data < 30:
                        print("Stop, obstacle in front")
                        self.rc_car.stop()
                    
                    #stop condition for the stop sign and check flag
                    elif 0 < self.d_stop_sign < 25 and stop_sign_active:
                        print("Stop sign ahead")
                        self.rc_car.stop()

                        # stop for 5 seconds
                        if stop_flag is False:
                            self.stop_start = cv2.getTickCount()
                            stop_flag = True
                        self.stop_finish = cv2.getTickCount()

                        self.stop_time = (self.stop_finish - self.stop_start)/cv2.getTickFrequency()
                        print "Stop time: %.2fs" % self.stop_time

                        # 5 seconds later, continue driving
                        if self.stop_time > 5:
                            print("Waited for 5 seconds")
                            stop_flag = False
                            stop_sign_active = False

                    #Turn right if the right sign is spotted
                    elif 0 < self.d_right_sign < 25:
                        print("Turn right ahead")
                        self.rc_car.steer(1)

                        #Turn right for a couple seconds
                        if turn_right_flag is False:
                            self.right_start = cv2.getTickCount()
                            turn_right_flag = True
                        self.right_finish = cv2.getTickCount()

                        self.right_time = (self.right_finish - self.right_start)/cv2.getTickFrequency()
                        print "Turn time: %.2fs" % self.right_time

                        if self.right_time > 2:
                            print('Turned right for 2 seconds')
                            self.d_right_sign = 25
                            turn_right_flag = False

                    #Turn left if the left sign is spotted
                    elif 0 < self.d_left_sign < 25:
                        print("Turn left ahead")
                        self.rc_car.steer(0)

                        #Turn left for a couple of seconds
                        if turn_left_flag is False:
                            self.left_start = cv2.getTickCount()
                            turn_left_flag = True
                        self.left_finish = cv2.getTickCount()

                        self.left_time = (self.left_finish - self.left_start)/cv2.getTickFrequency()
                        print "Turn time: %.2fs" % self.left_time

                        if self.left_time > 2:
                            print('Turned left for 2 seconds')
                            self.d_left_sign = 25
                            turn_left_flag = False

                    #Obselete code for traffic light
                    elif 0 < self.d_light < 30:
                        #print("Traffic light ahead")
                        if self.obj_detection.red_light:
                            print("Red light")
                            self.rc_car.stop()
                        elif self.obj_detection.green_light:
                            print("Green light")
                            pass
                        elif self.obj_detection.yellow_light:
                            print("Yellow light flashing")
                            pass
                        
                        self.d_light = 30
                        self.obj_detection.red_light = False
                        self.obj_detection.green_light = False
                        self.obj_detection.yellow_light = False
                    #Otherwise continue forward
                    else:
                        self.rc_car.steer(prediction)
                        self.stop_start = cv2.getTickCount()
                        self.d_stop_sign = 25

                        if stop_sign_active is False:
                            self.drive_time_after_stop = (self.stop_start - self.stop_finish)/cv2.getTickFrequency()
                            if self.drive_time_after_stop > 5:
                                stop_sign_active = True

                    if cv2.waitKey(1) & 0xFF == ord('q'):
                        self.rc_car.stop()
                        break

            cv2.destroyAllWindows()

        finally:
            print "Connection closed on thread 1"

"""
ThreadServer

Helper class for setting up threads with socketserver for handling requests.
"""
class ThreadServer(object):

    def server_thread(host, port):
        server = SocketServer.TCPServer((host, port), VideoStreamHandler)
        server.serve_forever()

    def server_thread2(host, port):
        server = SocketServer.TCPServer((host, port), SensorDataHandler)
        server.serve_forever()

    distance_thread = threading.Thread(target=server_thread2, args=('169.254.167.89', 8002))
    distance_thread.start()
    video_thread = threading.Thread(target=server_thread('169.254.167.89', 8000))
    video_thread.start()

#Override main with constructor call
if __name__ == '__main__':
    ThreadServer()






















