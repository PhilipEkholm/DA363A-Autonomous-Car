import serial
import SocketServer
import threading

class SerialMonitor(object):
    def __init__(self):
        self.ser = serial.Serial('/dev/ttyACM0', 115200, timeout=1)
        self.send_inst = True

    def is_number(self, number):
        try:
            if number == int(number):
                return True
        except Exception:
            return False

    def steer(self, command):
        for i in range(len(command)):
            number = int(command[i])
            if number >= 0 and number <= 9:
                self.ser.write(chr(number))

    def close(self):
        self.ser.close()

class ManualDriving(SocketServer.BaseRequestHandler):
    def handle(self):
        sm = SerialMonitor()
            
        while True:
            message = self.request.recv(1024).strip()

            if message is not None:
                print "{} wrote:".format(self.client_address[0])
                print message
                sm.steer(message)
        sm.close()

if __name__ == '__main__':
    server = SocketServer.TCPServer(('192.168.0.3', 8001), ManualDriving)
    server.serve_forever()






















    
