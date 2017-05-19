__author__ = 'Philip Ekholm'

import numpy as np
import cv2
import pygame
from pygame.locals import *
import socket
import SocketServer
import threading

class VideoStreamHandler(SocketServer.BaseRequestHandler):
	def handle(self):
		self.k = np.zeros((4, 4), 'float')
		for i in range(4):
			self.k[i, i] = 1
		self.temp_label = np.zeros((1, 4), 'float')

		saved_frame = 0
		total_frame = 0

		#Collect images for training
		print 'Start collecting images...'
		e1 = cv2.getTickCount()
		image_array = np.zeros((1, 38400))
		label_array = np.zeros((1, 4), 'float')

		try:
			stream_bytes = ' '
			frame = 1

			while True:
				stream_bytes += self.request.recv(1024)
				first = stream_bytes.find('\xff\xd8')
				last = stream_bytes.find('\xff\xd9')

				if first != -1 and last != -1:
					jpg = stream_bytes[first:last + 2]
					stream_bytes = stream_bytes[last +2]
					image = cv2.imdecode(np.fromstring(jpg, dtype=np.uint8), cv2.CV_LOAD_IMAGE_GRAYSCALE)

					#select lower half of the image
					#roi = image[120:240, :]

					#Save streamed images
					#cv2.imwrite('training_images/frame{:>05}.jpg'.format(frame), image)

					cv2.imshow('image', image)

					#Reshape into one row array
					#temp_array = roi.reshape(1, 38400).astype(np.float32)

					frame += 1
					total_frame += 1
		finally:
			print 'Connection closed on thread 1'

class GetInput(object):
	def __init__(self):
		host, port = '192.168.20.164', 8002

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

class ThreadServer(object):
	def stream_server(host, port):
		server = SocketServer.TCPServer((host, port), VideoStreamHandler)
		server.serve_forever()

	streamThread = threading.Thread(target=stream_server, args=('192.168.20.175', 8001))
	streamThread.start()

if __name__ == '__main__':
	#ThreadServer()
	GetInput()













