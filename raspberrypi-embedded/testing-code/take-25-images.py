from picamera import PiCamera
from time import sleep

camera = PiCamera()

camera.start_preview()
for i in range(25):
    sleep(8)
    camera.capture('/home/pi/Desktop/chess_board/image%s.jpg' % i)
camera.stop_preview()
