#include <SoftwareSerial.h>
#include <Braccio.h>
#include <Servo.h>

Servo base;
Servo shoulder;
Servo elbow;
Servo wrist_ver;
Servo wrist_rot;
Servo gripper;

int bluetoothTx = 2;  // TX-O pin of bluetooth mate, Arduino D2
int bluetoothRx = 3;  // RX-I pin of bluetooth mate, Arduino D3
 
int dataFromBt;

boolean binary = false;
 
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);
 
void setup()
{
  Braccio.begin();
  Serial.begin(9600);  
  bluetooth.begin(9600);
  
}
 
void loop()
{
 
  if (bluetooth.available()) // Bluetooth input
  {
    // Send any characters the bluetooth prints to the serial monitor
    dataFromBt = bluetooth.read();
    Serial.println((char)dataFromBt);

    if (dataFromBt == 'v') {
      Serial.println("Vorne");
      bluetooth.print("v");
      vorne();
    }
    if (dataFromBt == 'h') {
      Serial.println("Hinten");
      bluetooth.print("h");
      hinten();
    }
    /*if (dataFromBt == 'b') {
      binary = true;
    } else {
      binary = false;
    }*/
 
  }
 
  if (Serial.available()) // Serial input
  {
    bluetooth.print((char)Serial.read()); 
  }
 
 
  if (binary) {

    bluetooth.print("1");
    Serial.println("EINS");
    delay(1000);

    bluetooth.print("0");
    Serial.println("NULL");
    delay(1000);
  }
  
}

void vorne() {
    
  Braccio.ServoMovement (20,90,30,30,45,70,10);

}

void hinten() {

  Braccio.ServoMovement (20,90,180,30,45,70,73);
 
}


