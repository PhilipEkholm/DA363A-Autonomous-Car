int reversePin = 9;
int forwardPin = 8;
int leftPin = 7;
int rightPin = 6;

int order = 55;
int time = 75;

//control flag
int flag = 0;

void setup() {
  Serial.begin(115200);
  Serial.print("\n\nStart...\n");
  pinMode(13, OUTPUT);
}

void loop() {

  //get input
  if (Serial.available() > 0){
    order = Serial.read();
    Serial.print("I received: ");
    Serial.println(order);
    flag = 1;
  }

  if(flag == 1){
      flag = 0;
      digitalWrite(13, HIGH);
  }

}

