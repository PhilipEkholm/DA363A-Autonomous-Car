import socket
import sys
import pygame
from pygame.locals import *

class SocketRC(object):
	def __init__(self):
		HOST, PORT = "192.168.20.81", 8001

		# Create a socket (SOCK_STREAM means a TCP socket)
		#self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

		# Connect to server and send data
		#self.sock.connect((HOST, PORT))

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
    				    #self.sock.send(str(6))

    				elif key_input[pygame.K_UP] and key_input[pygame.K_LEFT]:
    				    print("Forward Left")
    				    #self.sock.send(str(7))

    				elif key_input[pygame.K_DOWN] and key_input[pygame.K_RIGHT]:
    				    print("Reverse Right")
    				    #self.sock.send(str(8))

    				elif key_input[pygame.K_DOWN] and key_input[pygame.K_LEFT]:
    				    print("Reverse Left")
    				    #self.sock.send(str(9))

    				# simple orders
    				elif key_input[pygame.K_UP]:
    				    print("Forward")
    				    #self.sock.send(str(1))

    				elif key_input[pygame.K_DOWN]:
    				    print("Reverse")
    				    #self.sock.send(str(2))

    				elif key_input[pygame.K_RIGHT]:
    				    print("Right")
    				    #self.sock.send(str(3))

    				elif key_input[pygame.K_LEFT]:
    				    print("Left")
    				    #self.sock.send(str(4))

    				# exit
    				elif key_input[pygame.K_x] or key_input[pygame.K_q]:
    				    print 'Exit'
    				    self.send_inst = False
    				    #self.sock.send(str(0))
    				    break

				elif event.type == pygame.KEYUP:
					#self.sock.send(str(0))
					print 'None'

if __name__ == '__main__':
    SocketRC()