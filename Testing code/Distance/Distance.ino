 //Distance.ino
 
 int inputPin=2; //ECHO pin 
 int outputPin=4; //TRIG pin 
 bool firstTime = true;

 void setup() { 
   Serial.begin(9600); 
   pinMode(inputPin, INPUT); 
   pinMode(outputPin, OUTPUT); 
 }
 
 void loop() { 
   digitalWrite(outputPin, HIGH); //Trigger ultrasonic detection 
   delayMicroseconds(10); 
   digitalWrite(outputPin, LOW); 
   int distance = pulseIn(inputPin, HIGH); //Read ultrasonic reflection
   distance= distance/58; //Calculate distance 
   int lastValue;

   if(firstTime){
      firstTime = false;
      lastValue = distance;
   }
   

   if(distance >= 200 || distance < 0){
      Serial.println("Out of distance");
   }
   else{
      Serial.println(distance); //Print distance  
   }

   lastValue = distance;
   
   delay(100); 
 } 












