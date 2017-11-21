#include <SoftwareSerial.h>
 
int bluetoothTx = 2;  // TX-O pin of bluetooth mate, Arduino D2
int bluetoothRx = 3;  // RX-I pin of bluetooth mate, Arduino D3
 

 
int dataFromBt;
 
boolean binary = false;
 
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);
 
void setup()
{
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

    if (dataFromBt == '1') {
      Serial.println("eins");
      bluetooth.print("1");
    }
    if (dataFromBt == '0') {
      Serial.println("null");
      bluetooth.print("0");
    }
    if (dataFromBt == 'b') {
      binary = true;
    } else {
      binary = false;
    }
 
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
