/*
* MotorDistanceServo 
* 
* By Philip E, Henrik F
* 2017-04-05 12:05
* 
* Program for stopping motor in the event of obstacle
* and allowing steering
*/

/* HC-SR04*/
#include <Servo.h>

Servo pointer;
const byte servoPin = 8;



// Echo
const int echoPin = 2; //ECHO pin 
const int triggerPin = 4; //TRIG pin 

/* Motor Driver */
const int dir1PinA = 5;  //Backwards pin
const int dir2PinA = 3; //Forwards pin
const int speedPinA = 9; // Needs to be a PWM pin to be able to control motor speed

void setup() {
   Serial.begin(9600); 
   pinMode(echoPin, INPUT); 
   pinMode(triggerPin, OUTPUT);

   //Define L298N Dual H-Bridge Motor Controller Pins

   pinMode(dir1PinA,OUTPUT);
   pinMode(dir2PinA,OUTPUT);
   pinMode(speedPinA,OUTPUT);
   pointer.attach(servoPin);
   steer(50);
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
   digitalWrite(triggerPin, HIGH); //Trigger ultrasonic detection 
   delayMicroseconds(10); 
   digitalWrite(triggerPin, LOW);

   int distance = pulseIn(echoPin, HIGH); //Read ultrasonic reflection
   distance= distance/58; //Calculate distance 

   if(distance >= 200 || distance < 0){
      Serial.println("Out of distance");
   }
   else{
      Serial.println(distance);
   }

   if(distance > 35){
      motor_run(125, 1);
   }
   else if(distance <= 35){
      motor_run(distance, 1); 
   }
   else if(distance <= 10){
      //motor_run(0, 1);
   }
   
 
  steer(50);
  
  
  delay(100);
}




