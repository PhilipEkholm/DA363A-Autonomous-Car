/*
* CarController 
* 
* By Philip E, Henrik F
* 2017-04-05 12:05
* 
* Program for stopping motor in the event of obstacle
* and allowing steering
*/

#include <Servo.h>

Servo pointer;
const byte servoPin = 8; 

/* Motor Driver */
const byte dir1PinA = 5;  //Backwards pin
const byte dir2PinA = 3; //Forwards pin
const byte speedPinA = 11; // Needs to be a PWM pin to be able to control motor speed
const byte motorSpeed = 200; //Change motor speed
int command = 0;
int time = 50;

void setup(){
   Serial.begin(115200); 
    
   //Define L298N Dual H-Bridge Motor Controller Pins

   pinMode(dir1PinA,OUTPUT);
   pinMode(dir2PinA,OUTPUT);
   pinMode(speedPinA,OUTPUT);
   pointer.attach(servoPin);

   motor_run(0, 1, 10);
   steer(50, 10);
}

void loop(){
    if (Serial.available() > 0){
      command = Serial.read();
    }
    
    send_command(command,time);
}

void reset(){
  motor_run(0, 0, 50);
  steer(60, 50);
}

/*
*   The golden method! Control the motor with
*   a certain speed (0 to 100). Direction: 0 reverse, 1 forward.
*/

void motor_run(int speed, int direction, int time){
    if(direction == 1){
        analogWrite(speedPinA, speed);//Sets speed variable via PWM 
        digitalWrite(dir1PinA, LOW);
        digitalWrite(dir2PinA, HIGH);
    }
    else if (direction == 0) {
        analogWrite(speedPinA, speed);
        digitalWrite(dir1PinA, HIGH);
        digitalWrite(dir2PinA, LOW);
    }

    delay(time);
}

void steer(int capacity, int time){
    if(capacity > 100 || capacity < 0){
       capacity = 60;
    }
    
    int adjustment = 10.24 * capacity;
    
    //Steering between 65 degrees and 105 degrees from car right.
    //(20 degrees from the normal)
    int pos = map(adjustment, 0, 1024, 65, 105);
    pointer.write(pos);

    delay(time);
}

/*void send_command(String command, int time){
    if(command.charAt(0) == '1'){
        motor_run(command.substring(1).toInt(), 1, time);
    }
    else if(command.charAt(0) == '2'){
        steer(command.substring(1).toInt(), time);
    }
    else if(command.charAt(0) == '3'){
        motor_run(command.substring(1).toInt(), 0, time);
    }
    else{
        Serial.println("Invalid command");
    }
}
*/
void send_command(int command, int time){
  switch (command){

     //reset command
     case 0: reset(); break;

     // single command
     case 1: motor_run(motorSpeed, 1, time); 
        break;
     case 2: motor_run(motorSpeed, 0, time);
        break;
     case 3: steer(0, time); 
        break;
     case 4: steer(100, time); 
        break;

     //combination command
     case 6: motor_run(motorSpeed, 1, time);
             steer(0, time);
             break;
     case 7: motor_run(motorSpeed, 1, time);
             steer(100,time);
             break;
     case 8: steer(0,time);
             motor_run(motorSpeed, 0, time);
             break;
     case 9: steer(100,time);
             motor_run(motorSpeed, 0, time);
             break;
     default: Serial.print("Invalid Command\n");
    }
}

