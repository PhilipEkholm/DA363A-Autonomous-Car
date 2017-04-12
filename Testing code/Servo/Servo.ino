#include <Servo.h>

Servo pointer;
const byte servoPin = 9;
int incomingByte = 0;
const int dir1PinA = 5;  //Backwards pin
const int dir2PinA = 3; //Forwards pin
const int speedPinA = 10; // Needs to be a PWM pin to be able to control motor speed

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pointer.attach(servoPin);
  pinMode(dir1PinA,OUTPUT);
   pinMode(dir2PinA,OUTPUT);
   pinMode(speedPinA,OUTPUT);
  steer(50);
  motor_run(1,30);
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

void motor_run(int speed, int direction){
    if(direction == 1){
        analogWrite(speedPinA, speed);//Sets speed variable via PWM 
        digitalWrite(dir1PinA, LOW);
        digitalWrite(dir2PinA, HIGH);
        Serial.println("Motor Forward");
    }
    else if (direction == 0) {
        analogWrite(speedPinA, speed);
        digitalWrite(dir1PinA, HIGH);
        digitalWrite(dir2PinA, LOW);
        Serial.println("Motor Reverse");
        Serial.println("   ");
    }
}

int counter = 0;

void loop() {
 steer(counter++);
 if (counter == 100) {
  counter = 0;
 }
 delay(100);
}
