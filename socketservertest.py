import SocketServer
import threading
import cv2
import numpy as np
import socket
import sys

class MyTCPHandler(SocketServer.StreamRequestHandler):
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


if __name__ == "__main__":
    server = SocketServer.TCPServer(('192.168.20.175', 9999), MyTCPHandler)
    server.serve_forever()