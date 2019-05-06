#include <Servo.h>
#include <Arduino_JSON.h>

// Buttons (updated automatically)
boolean aButton = false;
boolean bButton = false;
boolean xButton = false;
boolean yButton = false;

// Axes (range from -1.0 to 1.0, updated automatically)
double xAxis = 0.0;
double yAxis = 0.0;

// Declare variables here:
Servo servo1;
Servo servo2;

void robotInit() {
  // runs when Arduino boots, use instead of setup:
  servo1.attach(9);
  servo2.attach(10);
  
}

void disabledInit() {
  // runs when robot changes to disabled:
  servo1.write(0);
  servo2.write(0);
}

void disabledLoop() {
  // runs periodically when disabled:
  // example: servo.write(90);
}

void enabledInit() {
  // runs when robot changes to enabled:
  
}

void enabledLoop() {
  // runs periodically when enabled:
 servo1.write(mapDouble(yAxis,-1.0,1.0,10.0,170.0));
 servo2.write(mapDouble(xAxis,-1.0,1.0,10.0,170.0));
  
}

// Code to run serial comms
unsigned long lastControlPacket = -10000;
uint8_t packet[3];
boolean disabled = false;

void setup() {
  Serial.begin(19200);
  pinMode(13, OUTPUT);
  robotInit();
}

void loop() {
  if(Serial.available() > 0) {
    Serial.readBytesUntil((char)255, packet,4);
    lastControlPacket = millis();
    aButton = bitRead(packet[2], 0);
    bButton = bitRead(packet[2], 1);
    xButton = bitRead(packet[2], 2);
    yButton = bitRead(packet[2], 3);
    xAxis = byteToDouble(packet[0]);
    yAxis = byteToDouble(packet[1]);
  } 

  // has a decent amount of time elapsed since last packet?
  if(millis()-lastControlPacket > 200){
    if(!disabled) {
      disabled = true;
      disabledInit();
    } else {
      disabledLoop();
    }
    digitalWrite(13, true);
  } else {
    if(disabled){
      disabled = false;
      enabledInit();
    } else {
      enabledLoop();
    }
    // blink status
    digitalWrite(13, round(millis()/500) % 2 == 0);
  }
 delay(20);
}

double byteToDouble(uint8_t b) {
  return mapDouble((int8_t)b,-128,127,-1.0,1.0);
}

double mapDouble(double x, double in_min, double in_max, double out_min, double out_max) {
 return (double)(x - in_min) * (out_max - out_min) / (double)(in_max - in_min) + out_min;
}
