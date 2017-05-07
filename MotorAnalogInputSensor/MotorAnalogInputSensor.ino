// Declare L298N Dual H-Bridge Motor Controller directly since there is not a library to load.
#include <Servo.h>

Servo pointer;
const byte servoPin = 8;

const int dir1PinA = 5; //Motor forwards
const int dir2PinA = 3; // Motor backwards
const int speedInputPinA = A0; //Analog input speed
const int dirInputPinA = A1; //Analog input servo
const int speedPinA = 11; // Needs to be a PWM pin to be able to control motor speed
const int echoPin = 2; //ECHO pin 
const int triggerPin = 4; //TRIG pin 

int sensorValue0 = 0;
int sensorValue1 = 0;
int inputSpeed = 0;
int inputServo = 0;
void setup() {  // Setup runs once per reset
  // initialize serial communication @ 9600 baud:
  Serial.begin(9600);
  pinMode(echoPin, INPUT); 
   pinMode(triggerPin, OUTPUT);
  //Define L298N Dual H-Bridge Motor Controller Pins

  pinMode(dir1PinA,OUTPUT);
  pinMode(dir2PinA,OUTPUT);
  pinMode(speedPinA,OUTPUT);
  pinMode(speedInputPinA, INPUT);
  pinMode(dirInputPinA, INPUT);
  pointer.attach(servoPin);
  

}

/*
*   The golden method! Control the motor with
*   a certain speed (0 to 100). Direction: 0 reverse, 1 forward.
*/

void motor_run(int speed, int direction){
    if(direction == 1){
        analogWrite(speedPinA, speed);//Sets speed variable via PWM 
        digitalWrite(dir1PinA, LOW);
        digitalWrite(dir2PinA, HIGH);
        Serial.println("Motor forward");
        Serial.println(speed);
    }
    else if (direction == 0) {
        analogWrite(speedPinA, speed);
        digitalWrite(dir1PinA, HIGH);
        digitalWrite(dir2PinA, LOW);
        Serial.println("Motor Reverse");
        Serial.println("   ");
    }
}

void steer(int capacity){
    if(capacity > 85) { 
       capacity = 85;  
    }
    else if (capacity < 12) {
      capacity = 12;
    }
    int adjustment = 10.24 * capacity;
    //Steering between 75 degrees and 95 degrees from car right.
    int pos = map(adjustment, 0, 1024, 70, 90);
    pointer.write(pos);
}

void loop() {
  digitalWrite(triggerPin, HIGH); //Trigger ultrasonic detection 
   delayMicroseconds(10); 
   digitalWrite(triggerPin, LOW);
  sensorValue0 = analogRead(speedInputPinA);
  delay(2);
  sensorValue1 = analogRead(dirInputPinA);
  inputSpeed = map(sensorValue0, 0, 676, 0, 100);
  inputServo = map(sensorValue1, 0, 676, 0, 100);
  
  steer(inputServo);
 int distance = pulseIn(echoPin, HIGH); //Read ultrasonic reflection
   distance= distance/58; //Calculate distance 

  if(distance > 35){
      motor_run(inputSpeed, 1);
   }
   else if(distance <= 35){
      motor_run(distance, 1); 
   }
   else if(distance <= 10){
      motor_run(0, 1);
   }
  //Serial.println(inputSpeed);
  //Serial.println(inputSpeed);
  delay(100);
  

  //motor_run(inputSpeed, 1);
 
 }
