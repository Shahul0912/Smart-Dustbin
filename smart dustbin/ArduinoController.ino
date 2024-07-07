#include <Servo.h>

const int trigPin = 9;     // Ultrasonic sensor - Trig pin
const int echoPin = 10;    // Ultrasonic sensor - Echo pin
const int servoPin = 8;    // Servo motor pin
const int trigPinDepth = 11;   // Trig pin of ultrasonic sensor (change to your preferred pin)
const int echoPinDepth = 12;   // Echo pin of ultrasonic sensor (change to your preferred pin)
const float speedOfSound_cm_s = 34300.0;

bool isOpen=false;

Servo servo;
 // Variable to track if the servo is open

void setup() {
  Serial.begin(9600);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(trigPinDepth, OUTPUT);
  pinMode(echoPinDepth, INPUT);
  servo.attach(servoPin);
  
}

void loop() {
  long duration, distance;

  // Trigger ultrasonic sensor
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  // Measure distance
  duration = pulseIn(echoPin, HIGH);
  distance = duration * 0.034 / 2;

    // finding depth of box 
  digitalWrite(trigPinDepth, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPinDepth, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPinDepth, LOW);

  float distance_cm = (pulseIn(echoPinDepth, HIGH) / 2.0) * (speedOfSound_cm_s / 1000000.0);

  Serial.println(distance_cm);

  // Check if an object is detected near the dustbin (within 5 cm)
  if (distance < 5) {
       servo.write(0); // Open the lid   // Set isOpen to true
      delay(5000);    
      servo.write(90);

    
  } else {
         servo.write(90); // Close the lid
    
  }

  delay(1000); // Adjust delay according to your needs
}
