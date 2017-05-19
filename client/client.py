import socket

class ClientConnection(object):
	def __init__(self, host, port):
		self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.s.connect((host, port))

	def send(self, command):
		self.s.send(command)

	def close(self):
		self.s.close()

if __name__ == '__main__':
	con = ClientConnection('192.168.20.164', 8001)

	while True:
		n = raw_input('Enter a number\n')

		if n == 'q':
			break

	con.close()
	print 'Finished'