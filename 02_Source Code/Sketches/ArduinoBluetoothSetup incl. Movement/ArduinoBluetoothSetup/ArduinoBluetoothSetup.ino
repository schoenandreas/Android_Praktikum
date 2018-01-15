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
  //All the servo motors will be positioned in the "safety" position:
  //Base (M1):90 degrees
  //Shoulder (M2): 45 degrees
  //Elbow (M3): 180 degrees
  //Wrist vertical (M4): 180 degrees
  //Wrist rotation (M5): 90 degrees
  //gripper (M6): 10 degrees
  Braccio.begin();
  // Step Delay: a milliseconds delay between the movement of each servo.  Allowed values from 10 to 30 msec.
  // M1=base degrees. Allowed values from 0 to 180 degrees
  // M2=shoulder degrees. Allowed values from 15 to 165 degrees
  // M3=elbow degrees. Allowed values from 0 to 180 degrees
  // M4=wrist vertical degrees. Allowed values from 0 to 180 degrees
  // M5=wrist rotation degrees. Allowed values from 0 to 180 degrees
  // M6=gripper degrees. Allowed values from 10 to 73 degrees. 10: the toungue is open, 73: the gripper is closed.
  Braccio.ServoMovement (30,95,90,180,180,65,10);
  Serial.begin(9600); // Open data rate an sets serial port to 9600 bps  
  bluetooth.begin(9600);
  Braccio.ServoMovement (30,95,70,180,180,65,10);
  Braccio.ServoMovement (30,95,80,180,180,65,73);
  Braccio.ServoMovement (30,95,100,180,180,65,73);
  Braccio.ServoMovement (30,60,100,180,180,65,73);
  
}
 
void loop()
{
 
  if (bluetooth.available()) // Bluetooth input
  {
    // Send any characters the bluetooth prints to the serial monitor
    dataFromBt = bluetooth.read();
    Serial.println((char)dataFromBt);

    if (dataFromBt == 'w') {
      Serial.println("Vorne");
      bluetooth.print("w");
      vorne();
    }
    if (dataFromBt == 'y') {
      Serial.println("Hinten");
      bluetooth.print("y");
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


