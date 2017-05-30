"""
AutoComunication.py

Listen to inputs from the server evaulation about path and write it to serial.
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


class AutomaticDriving(SocketServer.StreamRequestHandler):
    """
    AutomaticDriving
    
    The class that listens to inputs through SocketServer
    
    Extends:
        SocketServer.StreamRequestHandler
    
    Variables:
        server {TCPServer} -- TCPServer object for listening
    """
    def handle(self):
        # self.request is the TCP socket connected to the client
        sm = SerialMonitor()
        
        while True:
            message = self.request.recv(1024).strip()
            if message is not None:
                print "{} wrote:".format(self.client_address[0])
                print message
                sm.steer(message)

#Main of the program, will override default main
if __name__ == '__main__':
    server = SocketServer.TCPServer(('169.254.230.248', 8001), AutomaticDriving)
    server.serve_forever()








