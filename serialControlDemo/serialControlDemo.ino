#include <Adafruit_MotorShield.h>
#include <Servo.h>
#include <Wire.h>
#include <Arduino_JSON.h>


Adafruit_MotorShield MotorShield = Adafruit_MotorShield();
Adafruit_DCMotor *leftmotor = MotorShield.getMotor(1);
Adafruit_DCMotor *rightmotor = MotorShield.getMotor(2);

// Buttons (updated automatically)
boolean aButton = false;
boolean bButton = false;
boolean xButton = false;
boolean yButton = false;

// Axes (range from -1.0 to 1.0, updated automatically)
double xAxis = 0.0;
double yAxis = 0.0;


// Declare variables here:

void robotInit() {
  Serial.println("boop"); //test message to verify serial comms
  MotorShield.begin();
}
void enabledInit() {
  
  
  //woosh
}

void enabledLoop() {

 
}

//
//
//
//
// everything below is serial comms code.
unsigned long lastControlPacket = -10000;
uint8_t packet[3];
boolean disabled = false;

void setup() {
  Serial.begin(19200);
  pinMode(13, OUTPUT);
  robotInit();
}

void loop() {
  if (Serial.available() > 0) {
    Serial.readBytesUntil((char)255, packet, 4);
    lastControlPacket = millis();
    aButton = bitRead(packet[2], 0);
    bButton = bitRead(packet[2], 1);
    xButton = bitRead(packet[2], 2);
    yButton = bitRead(packet[2], 3);
    xAxis = byteToDouble(packet[0]);
    yAxis = byteToDouble(packet[1]);
  }

  // has a timeout occured?
  if (millis() - lastControlPacket > 200) {
    if (!disabled) {
      disabled = true;
    }
    digitalWrite(13, true);
  } else {
    if (disabled) {
      disabled = false;
      enabledInit();
    } else {
      enabledLoop();
    }
    // blink status
    digitalWrite(13, round(millis() / 500) % 2 == 0);
  }
  delay(20);
}

double byteToDouble(uint8_t b) {
  return mapDouble((int8_t)b, -128, 127, -1.0, 1.0);
}

double mapDouble(double x, double in_min, double in_max, double out_min, double out_max) {
  return (double)(x - in_min) * (out_max - out_min) / (double)(in_max - in_min) + out_min;
}
