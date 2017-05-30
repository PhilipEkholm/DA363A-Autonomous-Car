"""
AndroidComms.py

A script for listening on a socket (in our case the Android-application).
"""

import serial
import SocketServer
import threading

"""
SerialMonitor

A class for creating a serial connection to the Arduino-card, will open COM-port on Raspberry pi as well as sanitize
inputs in order to avoid crashes from invalid inputs.
"""
class SerialMonitor(object):
    def __init__(self):
        """
        Constructor for the class, will initialize serial-connection
        """
        self.ser = serial.Serial('/dev/ttyACM0', 115200, timeout=1)
        self.send_inst = True

    def is_number(self, number):
        """
        Check is input number is int
        
        Since python uses dynamic assigning you need a way to detect the data-type.
        
        Arguments:
            number {unspecified} -- the number to check
        
        Returns:
            bool -- true if number is an int, otherwise false
        """
        try:
            if number == int(number):
                return True
        except Exception:
            return False

    def steer(self, command):
        """
        steer
        
        Send a command to the arduino by writing to the serial-port
        
        Arguments:
            command {char} -- the char of commands (string if there was a delay in the stream)
        """
        for i in range(len(command)):
            number = int(command[i])
            if number >= 0 and number <= 9:
                self.ser.write(chr(number))

    def close(self):
        """
        close
        
        Close the serial connection if the script is terminated
        """
        self.ser.close()

class ManualDriving(SocketServer.BaseRequestHandler):
    """
    ManualDriving
    
    The class that listens to inputs through SocketServer
    
    Extends:
        SocketServer.BaseRequestHandler
    
    Variables:
        server {TCPServer} -- TCPServer object for listening
    """
    def handle(self):
        """
        handle
        
        listen to the socket and write is message is set to something
        """
        sm = SerialMonitor()
            
        while True:
            message = self.request.recv(1024).strip()

            if message is not None:
                print "{} wrote:".format(self.client_address[0])
                print message
                sm.steer(message)
        sm.close()

#Main of the program, will override default main

if __name__ == '__main__':
    server = SocketServer.TCPServer(('192.168.0.3', 8001), ManualDriving)
    server.serve_forever()






















    
