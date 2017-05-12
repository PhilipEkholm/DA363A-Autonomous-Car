__author__ = 'Philip Ekholm'

import numpy as np
import cv2
import serial
import pygame
from pygame.locals import *
import socket
import SocketServer
import threading

#Modified

class VideoStreamHandler(SocketServer.StreamRequestHandler):
    def handle(self):
        stream_bytes = ' '

        # create labels
        self.k = np.zeros((4, 4), 'float')
        for i in range(4):
            self.k[i, i] = 1
        self.temp_label = np.zeros((1, 4), 'float')
        self.send_inst = True

        def collect_image(self):
            saved_frame = 0
            total_frame = 0

            print 'Start collecting images...'
            e1 = cv2.getTickCount()
            image_array = np.zeros((1, 38400))
            label_array = np.zeros((1, 4), 'float')

            try:
                stream_bytes = ' '
                frame = 1

                while
        finally:
            print "Connection closed on thread 1"


class ThreadServer(object):
    def server_thread(host, port):
        server = SocketServer.TCPServer((host, port), VideoStreamHandler)
        server.serve_forever()

    video_thread = threading.Thread(target=server_thread, args=('192.168.20.175', 8000))
    video_thread.start()

if __name__ == '__main__':
    ThreadServer()












