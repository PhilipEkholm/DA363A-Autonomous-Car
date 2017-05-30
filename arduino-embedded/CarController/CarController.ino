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
const byte SERVO_PIN = 8;  
 
/* Motor Driver */ 
const byte DIR1_PINA = 5;  //Backwards pin 
const byte DIR2_PINA = 3; //Forwards pin 
const byte SPEED_PINA = 11; // Needs to be a PWM pin to be able to control motor speed 
const byte MOTOR_SPEED = 195; //Change motor speed 

//Middle point of the servo
const byte MIDDLE_POINT_SERVO = 50;
int command = 0; 
int lastCommand = 0; 
int time = 50; 

/**
 *  Setup function which is called first in the program, used for configuration
 */

void setup(){ 
    //Serial runs at 115 200 Baud
	Serial.begin(115200);  
	 
	//Define L298N Dual H-Bridge Motor Controller Pins 

	pinMode(DIR1_PINA,OUTPUT); 
	pinMode(DIR2_PINA,OUTPUT); 
	pinMode(SPEED_PINA,OUTPUT); 
	pointer.attach(SERVO_PIN); 

    //Set default values (stop motor and keep wheels straight)
	motor_run(0, 1, 10); 
	steer(MIDDLE_POINT_SERVO, 10); 
} 

/**
 * Loop forever while the Arduino has power, will constantly listen
 * to serial-port for inputs, interprets command as well as stores it.
 */
 
void loop(){ 
    if (Serial.available() > 0){ 
      command = Serial.read(); 
    } 
     
    send_command(command,time); 
    lastCommand = command; 
} 

/**
 *  Set car at its default state if command 0 is passed
 */
void reset(){ 
	motor_run(0, 0, 50); 

	steer(MIDDLE_POINT_SERVO, 50);
} 
 
/**
 *  Controls the speed of the motor as well as the direction of the
 *  DC-motor.
 *   
 * @param   speed the speed of the motor to keep which is a value between 0 (stop) and 255 (full power)
 * @param   direction direction of the motor, where 1 means forward and 0 is backwards
 * @param   time embedded delay of operation, usually 50 milliseconds
 */

void motor_run(int speed, int direction, int time){ 
    if(direction == 1){ 
        analogWrite(SPEED_PINA, speed);//Sets speed variable via PWM  
        digitalWrite(DIR1_PINA, LOW); 
        digitalWrite(DIR2_PINA, HIGH); 
    } 
    else if (direction == 0) { 
        analogWrite(SPEED_PINA, speed); 
        digitalWrite(DIR1_PINA, HIGH); 
        digitalWrite(DIR2_PINA, LOW); 
    } 
 
    delay(time); 
} 

/**
 * @param   capacity capacity is the direction to steer from 0 to 100, where 0 is to
 *          the right viewed from the forward direction of the car.
 * @param   time embedded delay of operation, usually 50 milliseconds.
 */

void steer(int capacity, int time){ 
    if(capacity > 100 || capacity < 0){ 
       capacity = MIDDLE_POINT_SERVO; 
    } 
     
    int adjustment = 10.24 * capacity; 
     
    /*
    *   Steering was adjusted several times to meet the needs
    *   of accurate steering. The servo can be steered up to 
    *   55 degrees in one direction and 105 in the other,
    *   and is mapped accordingly to the map-function.
    */
    int pos = map(adjustment, 0, 1023, 55, 105);
    pointer.write(pos);
 
    delay(time); 
} 

/**
 * @param   command the command that was passed through serial-port
 * @param   time embedded delay of operation, usually 50 milliseconds
 */

void send_command(int command, int time){ 
  switch (command){ 
 
     //reset command 
     case 0: reset(); break; 
 
     // simple command 
     case 1: 
        //Forward
     	motor_run(MOTOR_SPEED, 1, time);
        break; 
     case 2: 
        //Backward
     	motor_run(MOTOR_SPEED, 0, time); 
        break; 
     case 3: 
        //Right
     	steer(0, time);  
        break; 
     case 4: 
        //Left
     	steer(100, time);  
        break; 
 
     //complex command 
     case 6: 
        //Forward right
     	motor_run(MOTOR_SPEED, 1, time); 
        steer(0, time); 
        break; 
     case 7: 
        //Forward left
     	motor_run(MOTOR_SPEED, 1, time); 
        steer(100,time); 
        break; 
     case 8: 
        //Backward right
     	steer(0,time); 
        motor_run(MOTOR_SPEED, 0, time); 
        break; 
     case 9: 
        //Backward left
     	steer(100,time);
        motor_run(MOTOR_SPEED, 0, time);
        break; 
     default: Serial.print("Invalid Command\n"); 
    } 
} 









