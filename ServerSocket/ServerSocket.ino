/*
 *  This sketch demonstrates how to set up a simple HTTP-like server.
 *  The server will set a GPIO pin depending on the request
 *    http://server_ip/gpio/0 will set the GPIO2 low,
 *    http://server_ip/gpio/1 will set the GPIO2 high
 *  server_ip is the IP address of the ESP8266 module, will be 
 *  printed to Serial when the module is connected.
 */

#include <ESP8266WiFi.h>

const char* ssid = "fille";
const char* password = "";
const int motorPin = 0;
const int servoPin = 15;

boolean alreadyConnected = false; // whether or not the client was connected previously

// Create an instance of the server
// specify the port to listen on as an argument
WiFiServer server(4444);

void setup() {
  Serial.begin(115200);
  delay(10);

  // prepare GPIO2
  pinMode(motorPin, OUTPUT);
  pinMode(servoPin, OUTPUT);
  
  // Connect to WiFi network
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  
  // Start the server
  server.begin();
  Serial.println("Server started");

  // Print the IP address
  Serial.println(WiFi.localIP());
}
//By making the client object global we can keep up the session
WiFiClient client;
boolean alreadyDone = false;

void loop() {
  
  // Check if a client has connected
  client = server.available();
  
  if (!client) {
    return;
  }
     
  // Wait until the client sends some data
  Serial.println("new client");
  while(!client.available()){
    delay(1);
  }

  String req = "";
  
  // Read the first line of the request
  req = client.readStringUntil('\r');
  Serial.println(req);
  steer_arduino(req);
  client.flush();
  
  client.flush();

  // Send the response to the client
  client.print("Response");
  
  delay(1);
  Serial.println("Client disonnected");

  // The client will actually be disconnected 
  // when the function returns and 'client' object is detroyed  
  
}

int steer_arduino(String code){
  int percentage = code.substring(1).toInt();
  percentage *= 10;

  if(code.charAt(0) == '1'){
      analogWrite(motorPin, percentage);
  }
  else if(code.charAt(0) == '2'){
      analogWrite(servoPin, percentage);
  }
}





