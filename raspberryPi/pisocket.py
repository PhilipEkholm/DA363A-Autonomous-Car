import socket #for sockets
import sys

try:
    #Create an AF_INET socket with TCP Stream
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
except socket.error, msg:
    print('Failed to create a socket, error code: ' + str(msg[0]) + ' , Error message: ' + msg[1])
    sys.exit()

print 'Socket Created'

#IP-Address
host = '192.168.1.11'
port = 4444

try:
    remote_ip = socket.gethostbyname(host);
except socket.gaierror:
    #Could not resolve
    print('Hostname could not be resolved, Exiting')
    sys.exit()

print('IP-Address of ' + host + ' is ' + remote_ip)

#Connect to remote server
s.connect((remote_ip, port))

print('Socket connected to ' + host + ' on ip ' + remote_ip)

#Send test message
message = 'Hello, World!'

try:
    #Set the whole string
    s.sendall(message)
except:
    #Send failed
    print('Send failed')
    sys.exit()

print('Message sent successfully')
















