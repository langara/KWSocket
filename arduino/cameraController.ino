#include <Servo.h>
 
Servo servo;

int pos = 180;
int goLeftPin = 12;
int goRightPin = 13;

void setup() {
 servo.attach(9);
 pinMode(goLeftPin, INPUT);  
 pinMode(goRightPin, INPUT);
 servo.write(pos); 
 Serial.begin(9600);
}
 
void loop() {
  if (digitalRead(goRightPin)) {
      Serial.write("Go-right\n");
      pos = pos - 2;
      servo.write(pos);
      
  } else if (digitalRead(goLeftPin)) {
      Serial.write("Go-left\n");
      pos = pos + 2;
      servo.write(pos);
  }
  delay(50);
}
