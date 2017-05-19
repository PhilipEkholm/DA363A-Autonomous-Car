import SocketServer
import threading
import cv2
import numpy as np
import socket
import sys
import threading
import pygame
from pygame.locals import *

class AndroidHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        self.data = self.request.recv(1024)
        print "{} wrote:".format(self.client_address[0])
        print self.data

        self.request.send(self.data.upper())

class GetInput(object):
    def __init__(self):
        host, port = '192.168.20.164', 8001

        # Create a socket (SOCK_STREAM means a TCP socket)
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((host, port))

        pygame.init()
        self.send_inst = True
        self.steer()

    def steer(self):
        while self.send_inst:
            for event in pygame.event.get():
                if event.type == KEYDOWN:
                    key_input = pygame.key.get_pressed()

                    #Complex orders
                    if key_input[pygame.K_UP] and key_input[pygame.K_RIGHT]:
                        #6
                        print("Forward Right")
                        self.sock.send(str(6))
                    elif key_input[pygame.K_UP] and key_input[pygame.K_LEFT]:
                        #7
                        print("Forward Left")
                        self.sock.send(str(7))
                    elif key_input[pygame.K_DOWN] and key_input[pygame.K_RIGHT]:
                        #8
                        print("Reverse Right")
                        self.sock.send(str(8))
                    elif key_input[pygame.K_DOWN] and key_input[pygame.K_LEFT]:
                        #9
                        print("Reverse Left")
                        self.sock.send(str(9))

                    # simple orders
                    elif key_input[pygame.K_UP]:
                        #1
                        print("Forward")
                        self.sock.send(str(1))
                    elif key_input[pygame.K_DOWN]:
                        #2
                        print("Reverse")
                        self.sock.send(str(2))
                    elif key_input[pygame.K_RIGHT]:
                        #3
                        print("Right")
                        self.sock.send(str(3))
                    elif key_input[pygame.K_LEFT]:
                        #4
                        print("Left")
                        self.sock.send(str(4))

                    #Exit
                    elif key_input[pygame.K_x] or key_input[pygame.K_q]:
                        #0
                        print 'Exit'
                        self.send_inst = False
                        self.sock.send(str(0))
                        break

                elif event.type == pygame.KEYUP:
                    #0
                    print 'None'
                    self.sock.send(str(0))

class VideoStreamHandler(SocketServer.StreamRequestHandler):
    def handle(self):
        stream_bytes = ' '
        
        while True:
            stream_bytes += self.rfile.read(1024)
            first = stream_bytes.find('\xff\xd8')
            last = stream_bytes.find('\xff\xd9')

            if first != -1 and last != -1:
                jpg = stream_bytes[first:last+2]
                stream_bytes = stream_bytes[last+2:]
                gray = cv2.imdecode(np.fromstring(jpg, dtype=np.uint8), cv2.CV_LOAD_IMAGE_GRAYSCALE)
                image = cv2.imdecode(np.fromstring(jpg, dtype=np.uint8), cv2.CV_LOAD_IMAGE_UNCHANGED)

                cv2.imshow('image', image)

                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break

class ThreadingClass(object):
    def video_stream(host, port):
        server = SocketServer.TCPServer((host, port), VideoStreamHandler)
        server.serve_forever()

    def android_socket(host, port):
        server = SocketServer.TCPServer((host, port), AndroidHandler)
        server.serve_forever()

    video_thread = threading.Thread(target=video_stream, args=('192.168.20.175', 8000))
    video_thread.start()

    android_thread = threading.Thread(target=android_socket, args=('192.168.20.175', 9999))
    #android_thread.start()

if __name__ == "__main__":
    ThreadingClass()
    GetInput()







