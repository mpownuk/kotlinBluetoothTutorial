#include <SoftwareSerial.h>
SoftwareSerial BTSerial(10,11);

const int numLeds = 4;
const int ledPins[numLeds] = {4,5,6,7};

int data = 4;

void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600);  

for (int i = 0; i< numLeds; i++){
    pinMode(ledPins[i], OUTPUT);
  }
}

void loop() {
  if(BTSerial.available()){
    String receivedData = BTSerial.readStringUntil('\n');
    receivedData.trim();
    if(receivedData.length() > 0){
      data = receivedData.toInt();
      Serial.println(data);
    }

    for(int i = 0; i<numLeds; i++){
      digitalWrite(ledPins[i], LOW);
    }

    for (int i=0; i<numLeds; i++){
      if(data == ledPins[i]) {
        digitalWrite(ledPins[i], HIGH);
      }
    }
  }
}
