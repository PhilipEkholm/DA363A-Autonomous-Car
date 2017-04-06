#include <Servo.h>

Servo pointer;
const byte servoPin = 9;
int incomingByte = 0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pointer.attach(servoPin);
  steer(50);
}

/*
 *  Method will accept a value between 0 and 100 for steering.
 *  0% means max steering capacity to the right and 100% means
 *  max steering capacity to the left.
 *  
 *  Use 50 % for steering forward.
 */

void steer(int capacity){
    if(capacity > 100 || capacity < 0){
       capacity = 50;  
    }
    
    int adjustment = 10.24 * capacity;
    //Steering between 75 degrees and 95 degrees from car right.
    int pos = map(adjustment, 0, 1024, 75, 90);
    pointer.write(pos);
}

int counter = 0;

void loop() {
    
}
