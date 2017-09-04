#include <Servo.h>
 
Servo servo;

int pos = 180;
int goLeftPin = 13;
int goRightPin = 12;
int goRight = 0;
int goLeft = 0;

void setup() {
    servo.attach(9);
    pinMode(goLeftPin, INPUT);
    pinMode(goRightPin, INPUT);
}

void loop() {
  goRight = digitalRead(goRightPin);
  goLeft = digitalRead(goLeftPin);

  if (goRight != goLeft) {
     if (goRight) {
        if (pos > 60){
            pos = pos - 1;
            servo.write(pos);
          }
     } else if (goLeft) {
        if (pos < 180) {
            pos = pos + 1;
            servo.write(pos);
        }
    }
  }
  delay(25);
}