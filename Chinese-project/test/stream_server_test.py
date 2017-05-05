__author__ = 'Philip Ekholm'

import numpy as np
import cv2
import socket
import serial
import threading
import pygame
from pygame.locals import *

class VideoStreamingTest(object):
    def __init__(self):

        self.server_socket = socket.socket()
        self.server_socket.bind(('192.168.20.175', 8002))
        self.server_socket.listen(0)
        self.connection, self.client_address = self.server_socket.accept()
        self.connection = self.connection.makefile('rb')
        self.streaming()

    def streaming(self):

        try:
            print "Connection from: ", self.client_address
            print "Streaming..."

            stream_bytes = ' '
            while True:
                stream_bytes += self.connection.read(1024)
                first = stream_bytes.find('\xff\xd8')
                last = stream_bytes.find('\xff\xd9')
                if first != -1 and last != -1:
                    jpg = stream_bytes[first:last + 2]
                    stream_bytes = stream_bytes[last + 2:]
                    image = cv2.imdecode(np.fromstring(jpg, dtype=np.uint8), cv2.CV_LOAD_IMAGE_UNCHANGED)
                    cv2.imshow('image', image)
        finally:
            self.connection.close()
            self.server_socket.close()

class SteerCar(object):
    def __init__(self):
        HOST, PORT = "192.168.20.164", 8002

        # Create a socket (SOCK_STREAM means a TCP socket)
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        # Connect to server and send data
        self.sock.connect((HOST, PORT))

        pygame.init()
        self.send_inst = True
        self.steer()

    def steer(self):
        while self.send_inst:
            for event in pygame.event.get():
                if event.type == KEYDOWN:
                    key_input = pygame.key.get_pressed()

                    # complex orders
                    if key_input[pygame.K_UP] and key_input[pygame.K_RIGHT]:
                        print("Forward Right")
                        self.sock.send(str(6))

                    elif key_input[pygame.K_UP] and key_input[pygame.K_LEFT]:
                        print("Forward Left")
                        self.sock.send(str(7))

                    elif key_input[pygame.K_DOWN] and key_input[pygame.K_RIGHT]:
                        print("Reverse Right")
                        self.sock.send(str(8))

                    elif key_input[pygame.K_DOWN] and key_input[pygame.K_LEFT]:
                        print("Reverse Left")
                        self.sock.send(str(9))

                    # simple orders
                    elif key_input[pygame.K_UP]:
                        print("Forward")
                        self.sock.send(str(1))

                    elif key_input[pygame.K_DOWN]:
                        print("Reverse")
                        self.sock.send(str(2))

                    elif key_input[pygame.K_RIGHT]:
                        print("Right")
                        self.sock.send(str(3))

                    elif key_input[pygame.K_LEFT]:
                        print("Left")
                        self.sock.send(str(4))

                    # exit
                    elif key_input[pygame.K_x] or key_input[pygame.K_q]:
                        print 'Exit'
                        self.send_inst = False
                        self.sock.send(str(5))
                        break

                elif event.type == pygame.KEYUP:
                    self.sock.send(str(0))
                    print 'None'

class ThreadServer(object):
    def collect_thread():
        VideoStreamingTest()

    task = threading.Thread(target=collect_thread)
    task.start()

if __name__ == '__main__':
    ThreadServer()








