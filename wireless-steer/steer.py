__author__ = 'Philip Ekholm'

import numpy as np
import cv2
import pygame
from pygame.locals import *
import socket
import SocketServer
import threading

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

if __name__ == '__main__':
	#ThreadServer()
	GetInput()













