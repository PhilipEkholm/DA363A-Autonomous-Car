void setup() {
  pinMode(0, OUTPUT);
  pinMode(15, OUTPUT);
  pinMode(2, OUTPUT);
  pinMode(16, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  analogWrite(0, 512);
  analogWrite(15, 512);
  analogWrite(2, 512);
  analogWrite(16, 512);
  delay(2000);
  analogWrite(0, 0);
  analogWrite(15, 0);
  analogWrite(2, 0);
  analogWrite(16, 0);
  delay(2000);
}
