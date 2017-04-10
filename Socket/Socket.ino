
/*
 *  Simple HTTP get webclient test
 */

#include <ESP8266WiFi.h>

const char* ssid     = "honor";
const char* password = "abcdefg1";

const char* host = "wifitest.adafruit.com";
const char* remoteIp = "192.168.1.6";

// Use WiFiClient class to create TCP connections
WiFiClient client;

void setup() {
  Serial.begin(115200);
  delay(100);

  // We start by connecting to a WiFi network

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
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

int value = 0;

void loop() {
  delay(500);
  ++value;

  Serial.print("connecting to ");
  Serial.println(remoteIp);

  if (!client.connect(remoteIp, 4444)) {
    Serial.println("connection failed");
    return;
  }
  
  // This will send the request to the server
  client.print("Hello world!");
  delay(500);
  
  // Read all the lines of the reply from server and print them to Serial
  while(client.available()){
    String line = client.readStringUntil('\r');
    Serial.print(line);
  }
  
  Serial.println();
  Serial.println("closing connection");
}

void sendMessage(String toSend){

    if(client){
        client.println(toSend+'\n');
        client.flush();
        Serial.println("Sendt message: "+toSend);
    }
    else{
        Serial.println("Could not send message; Not connected.");
    }
}
