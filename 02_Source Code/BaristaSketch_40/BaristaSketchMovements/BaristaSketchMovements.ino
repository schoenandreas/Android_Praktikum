#include <SoftwareSerial.h>
#include <Braccio.h>
#include <Servo.h>
#include "Wire.h"
#include "Adafruit_TCS34725.h"

Servo base;
Servo shoulder;
Servo elbow;
Servo wrist_ver;
Servo wrist_rot;
Servo gripper;

int underarm=wrist_ver.read()-180;
int wrist=wrist_rot.read()+180;

// Standard position
const int ba = 95;
const int sh = 90;
const int el = 140;
const int wrV = 180;
const int wrR = 65;
const int gr = 10;

//
const int colourArraySize = 7; 
const String colours[colourArraySize] = { "tomato", "blue", "water", "fanta"};

int bluetoothTx = 2;  // TX-O pin of bluetooth mate, Arduino D2
int bluetoothRx = 3;  // RX-I pin of bluetooth mate, Arduino D3
 
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);

// Color Sensor-Objekt initialisieren
// Parameter siehe: https://learn.adafruit.com/adafruit-color-sensors/program-it
Adafruit_TCS34725 tcs = Adafruit_TCS34725(TCS34725_INTEGRATIONTIME_50MS, TCS34725_GAIN_1X);
 
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
  //Braccio.ServoMovement (30,92,150,180,20,90,10);

  Serial.begin(9600); // Open data rate an sets serial port to 9600 bps  
  bluetooth.begin(9600);

  // Überprüfen, ob Color Sensor sich auch zurückmeldet
  if (tcs.begin()) {
    Serial.println("Sensor gefunden");
  } else {
    Serial.println("TCS34725 nicht gefunden ... Ablauf gestoppt!");
    while (1)
    delay(100); // Halt!
  } 


}

 
void loop(){
  // send data only when you receive data
  if (bluetooth.available()){
    String btString = "";
    int btStringInt = 0;  // nur für patterns
    String btJointString = "";
    int btDegree = 0;
    String btOrientation = "";

    // Send any characters the bluetooth prints to the serial monitor
    btString = bluetooth.readString();
    //btString = Serial.readString();
    Serial.println(btString);
    btString.trim();  // removes possible spaces Important for String comparison!
    btString.toLowerCase();
    btStringInt = btString.toInt(); //nur für patterns

    // Check for drink commands !!Is Working!!
    for (int i = 0; i < colourArraySize; i++){
      if (btString.equalsIgnoreCase(colours[i])){
        Serial.println("Braccio is searching for: " + btString);
        searchBeverage(btString);
        break;
      }
    }


    // Check for pattern commands
    switch(btStringInt){
      case 0:
        helloPattern();
        Serial.println("Braccio is performing Pattern Hello(0)!");
        break;
        
      case 1:
        goodbyePattern();
        Serial.println("Braccio is performing Pattern Goodbye(1)!");
        break;
        
      case 2:
        stretchPattern();
        break;
        
      case 3:
        crocodilePattern();
        break;
        
      case 4:
        grapPattern();
        break;

      case 5:
        stretchPattern();
        Serial.println("Braccio is performing Pattern Stretch(5)!");
        break;

      case 6:
        startPattern(0,10);
        Serial.println("Braccio is performing Pattern Start(6)!");
        break;

        //default:
        //currentPosition();
      
    }
    

    // Check for single Joint commands; Commands are either in the form "base_90/right." or "base_90."
    // Direct string comparison with  if (strcmp(dataFromBt, "") == 0) --> true
    if (btString.indexOf("_") > 0){
      btJointString = btString.substring(0, btString.indexOf("_"));
      Serial.println("The extracted btJointString is: " + btJointString);

      if(btString.indexOf("/") > 0){
        Serial.println("Relative command initiated!");
        btDegree = btString.substring(btString.indexOf("_")+1, btString.indexOf("/")).toInt();
        btOrientation = btString.substring(btString.indexOf("/")+1, btString.indexOf("."));
        Serial.print("The Joint " + btJointString + " is chosen. It turns to the relatively " + btOrientation + "position degree ");
        Serial.println(btDegree);
        singleCommandRelative(btJointString, btOrientation, btDegree);
        bluetooth.print("Relative command working");
        
        
      } else if (btString.indexOf(".") > 0){
        Serial.println("Absolute command initiated!");  
        btDegree = btString.substring(btString.indexOf("_")+1, btString.indexOf(".")).toInt();
        Serial.print("The Joint " + btJointString + " is chosen. It turns to the absolute position degree ");
        Serial.println(btDegree);
        singleCommandAbsolute(btJointString, btDegree); 
        bluetooth.print("Absolute command working");
        
      }  
    }
  }

  // Returns console content to bluetooth
  if (Serial.available()) // Serial input
  {
    bluetooth.print((char)Serial.read()); 
  }
}

void currentPosition (){
  Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()); //current position
}

void singleCommandAbsolute (String joint, int degree){
  Serial.println(joint);
  Serial.println(degree);
  Serial.println(base.read());
  Serial.println(shoulder.read());
  Serial.println(elbow.read());
  Serial.println("--------");
  Serial.println(wrist_ver.read());
  Serial.println(wrist_rot.read());
  Serial.println(gripper.read());
  

  if (joint.equalsIgnoreCase("base")){
    Braccio.ServoMovement (30,degree,shoulder.read(),elbow.read(),underarm,wrist,gripper.read());
    Serial.println("sCA base");
  } else if (joint.equalsIgnoreCase("shoulder")){
    Braccio.ServoMovement (30,base.read(),degree,elbow.read(),underarm,wrist,gripper.read());
    Serial.println("sCA shoulder");
  } else if (joint.equalsIgnoreCase("elbow")){
    Braccio.ServoMovement (30,base.read(),shoulder.read(),degree,underarm,wrist,gripper.read());
    Serial.println("sCA elbow");
  } else if (joint.equalsIgnoreCase("underarm")){
    Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),degree,wrist,gripper.read());
    underarm=degree;
    Serial.println("sCA underarm");
  } else if (joint.equalsIgnoreCase("wrist")){
    Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),underarm,degree,gripper.read());
    wrist=degree;
    Serial.println("sCA wrist");
  } else if (joint.equalsIgnoreCase("gripper")){
    Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),underarm,wrist,degree);
    Serial.println("sCA gripper");
  } else {
    currentPosition();
    Serial.println("No command matched in singleCommandAbsolute");
  }
}

void singleCommandRelative (String joint, String orientation, int degree){
  Serial.println(joint);
  Serial.println(degree);
  Serial.println(orientation);
  Serial.println(base.read());
  Serial.println(shoulder.read());
  Serial.println(elbow.read());
  Serial.println(wrist_ver.read());
  Serial.println(wrist_rot.read());
  Serial.println(gripper.read());
   if (joint.equalsIgnoreCase("base")){
    if (orientation.equalsIgnoreCase("right")) {
        Braccio.ServoMovement (30,base.read()+degree,shoulder.read(),elbow.read(),underarm,wrist,gripper.read()); // max 180               
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read()-degree,shoulder.read(),elbow.read(),underarm,wrist,gripper.read()); // min 0
      } else {
        // Only used if necessary for debugging
      }
  } else if (joint.equalsIgnoreCase("shoulder")){
    if (orientation.equalsIgnoreCase("right")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read()-degree,elbow.read(),underarm,wrist,gripper.read()); // min 15
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read()+degree,elbow.read(),underarm,wrist,gripper.read()); // max 165
      } else {
        // Only used if necessary for debugging
      }
  } else if (joint.equalsIgnoreCase("elbow")){
    if (orientation.equalsIgnoreCase("right")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read()-degree,underarm,wrist,gripper.read()); // min 15
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read()+degree,underarm,wrist,gripper.read()); // max 165
      } else {
        // Only used if necessary for debugging
      }
    // ToDo

    //funktioniert nicht sehr gut
  } else if (joint.equalsIgnoreCase("underarm")){
    if (orientation.equalsIgnoreCase("right")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),underarm-degree,wrist,gripper.read()); // min 15
        underarm=underarm-degree;
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),underarm,wrist,gripper.read()); // max 165
        underarm=underarm+degree;
      } else {
        // Only used if necessary for debugging
      }
    // ToDo

    //funktioniert nicht sehr gut
  } else if (joint.equalsIgnoreCase("wrist")){
    if (orientation.equalsIgnoreCase("right")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),underarm,wrist-degree,gripper.read()); // max 180      
        wrist=wrist-degree;         
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),underarm,wrist+degree,gripper.read()); // min 0
        wrist=wrist+degree;
      } else {
        // Only used if necessary for debugging
      }
    // ToDo
  } else if (joint.equalsIgnoreCase("gripper")){
    if (orientation.equalsIgnoreCase("open")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),underarm,wrist,gripper.read()+degree); // max 73
      } else if (orientation.equals("down")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read()-180,wrist,gripper.read()-degree); // min 10
      } else {
        // Only used if necessary for debugging
      }
  } else {
    currentPosition();
    Serial.println("No command matched in singleCommandRelative");
  }
}



String checkColour (){
   // Der Sensor liefert Werte für R, G, B und einen Clear-Wert zurück
   uint16_t clearcol, red, green, blue;
   float average, r, g, b;
   String colour = "";
   delay(100); // Farbmessung dauert c. 50ms 
   tcs.getRawData(&red, &green, &blue, &clearcol);
   
   // Mein Versuch einer Farbbestimmung für 
   // die 5 M&M-Farben Rot, Grün, Blau, Orange und Gelb
   
   // Durchschnitt von RGB ermitteln
   average = (red+green+blue)/3;
   // Farbwerte durch Durchschnitt, 
   // alle Werte bewegen sich jetzt rund um 1 
   r = red/average;
   g = green/average;
   b = blue/average;
   
   // Clear-Wert und r,g,b seriell ausgeben zur Kontrolle
   // r,g und b sollten sich ca. zwischen 0,5 und 1,5 
   // bewegen. Sieht der Sensor rot, dann sollte r deutlich über 1.0
   // liegen, g und b zwischen 0.5 und 1.0 usw.
   Serial.print("\tClear:"); Serial.print(clearcol);
   Serial.print("\tRed:"); Serial.print(r);
   Serial.print("\tGreen:"); Serial.print(g);
   Serial.print("\tBlue:"); Serial.print(b);
   
   // Versuch der Farbfeststellung anhand der r,g,b-Werte.
   // Am besten mit Rot, Grün, Blau anfangen die die Schwellenwerte
   // mit der seriellen Ausgabe entsprechend anpassen
   if ((r > 1.4) && (g < 0.9) && (b < 0.9)) {
    Serial.println("\tROT");
    colour = "tomato";
    return colour;
   } else if ((r > 0.5) && (r < 0.8) && (g > 0.95) && (g < 1.05) && (b > 1.1) && (b < 0.9)) { // r < 0.8 && g< 1.2
    Serial.println("\tBLAU");
    colour = "blue";
    return colour;
   }
   // Gelb und Orange sind etwas tricky, aber nach etwas
   // Herumprobieren haben sich bei mir diese Werte als
   // gut erwiesen
   else if ((r > 1.25) && (r < 1.35) && (g > 1.05) && (g < 1.15) && (b > 0.55) && (b < 0.65)) {
    Serial.println("\tGELB");
    colour = "fanta";
    return colour;
   }
   else if ((r > 0.95) && (r < 1.1) && (g > 1.0) && (g < 1.1) && (b > 0.85) && (b < 1.0)) {
    Serial.println("\tWEISS");
    colour = "water";
    return colour;
   } 
   // Wenn keine Regel greift, dann ehrlich sein
   else {
    Serial.println("\tNICHT ERKANNT"); 
    colour = "no";
    return colour;
   }
}

void searchBeverage (String btColour) {
    int moveBase = 0;
    startPattern(moveBase,10);
    //Braccio.ServoMovement (30,ba,sh,el,wrV,wrR,gr); // Standard: (30,95,90,140,180,65,10)

    for (int i = 0; i<4; i++){
      scanSidePattern(moveBase,10);
      if (checkColour() == btColour){  
          Serial.println("Braccio found a beverage with the right color!");
  
          scanSidePattern(moveBase,60);
          startPattern(moveBase,60);
          startPattern(80,60);
          scanSidePattern(80,60);
          scanSidePattern(80,10);
          break;
      } else {
        startPattern(moveBase,10);
      }
      startPattern(moveBase,10);
      moveBase += 20;
    }
    startPattern(80,10);
    startPattern(0,10);

    
}

void stretchPattern() {
  Braccio.ServoMovement (30,90, 90, 90, 90, 90,  73); // Stretched Arm position
}

void standardPattern() {
  Braccio.ServoMovement (30,ba,sh,el,wrV,wrR,gr);
}

void scanSidePattern(int base, int gripper) {
  Braccio.ServoMovement (30,90+base,145,180,30,89,gripper);
}

void scanSideUpPattern(int base, int gripper) {
  Braccio.ServoMovement (30,90+base,110,180,65,89, gripper);
}
void startPattern(int base, int gripper){
  Braccio.ServoMovement (30,90+base,90,180,90,89,gripper);
  }
void scanTopPattern() {
  Braccio.ServoMovement (30,92,90,180,180,90,10);
}

void helloPattern () {
  Braccio.ServoMovement (20,90,90,90,135,90,73);
  Braccio.ServoMovement (20,140,90,90,135,180,10);
  Braccio.ServoMovement (20,90,90,90,135,90,73);
  Braccio.ServoMovement (20,140,90,90,135,180,10);
  Braccio.ServoMovement (20,90,90,90,135,90,73);

}

void goodbyePattern () {
    Braccio.ServoMovement (20,90,90,180,135,60,73);
    delay(2000);
    Braccio.ServoMovement (20,90,90,90,135,60,73);
    Braccio.ServoMovement (20,140,90,180,135,60,73);
    delay(2000);
    Braccio.ServoMovement (20,140,90,90,135,60,73);
    Braccio.ServoMovement (20,90,90,90,135,60,73);

  
}

void colourLoop(){
  scanSidePattern(80,10);
  while(1){
  checkColour();
  delay(500);
  }
  }

// Imitate "Schnappi" hand
void crocodilePattern() {
  for (int i = 0; i < 5; i++){
    Braccio.ServoMovement (30,95,90,140,90,65,73);
    Braccio.ServoMovement (30,95,90,140,90,65,10);
  }
}

void grapPattern() {
  Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),10);
  Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),73);
}

void releasePattern() {
  Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),73);
  Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),10);
}

/*
Archive
  Braccio.ServoMovement (30,95,135,140,180,65,10); // Check durch sensor
  Braccio.ServoMovement (30,95,135,140,180,65,73); // greifen
  Braccio.ServoMovement (30,95,90,140,180,65,73); // greifen hoch
  Braccio.ServoMovement (30,20,90,140,180,65,73); // greifen hoch links
  Braccio.ServoMovement (30,20,135,140,180,65,73); // greifen hoch links runter
  Braccio.ServoMovement (30,20,135,140,180,65,10); // greifen hoch links runter loslassen
  Braccio.ServoMovement (30,20,90,140,180,65,10); // hoch
  Braccio.ServoMovement (30,95,90,140,180,65,10); // standard

  void singleCommandAbsolute (char joint, long degree){
  switch (joint){
    case 'b':
      Braccio.ServoMovement (30,degree,shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read());
      break;
      
    case 's':
      Braccio.ServoMovement (30,base.read(),degree,elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read());
      break;
      
    case 'e':
      Braccio.ServoMovement (30,base.read(),shoulder.read(),degree,wrist_ver.read(),wrist_rot.read(),gripper.read());
      break;
      
    case 'u':
      Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),degree,wrist_rot.read(),gripper.read());
      break;
      
    case 'w':
      Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),degree,gripper.read());
      break;
      
    case 'g':
      Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),degree);
      break;
      
    default: 
      currentPosition();
  }
}

void singleCommandRelative (char joint, String orientation, int degree){
   switch (joint){
    case 'b':
      if (orientation.equalsIgnoreCase("right")) {
        Braccio.ServoMovement (30,base.read()+degree,shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()); // max 180
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read()-degree,shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()); // min 0
      } else {
        // Only used if necessary for debugging
      }
      break;
      
    case 's':
      if (orientation.equalsIgnoreCase("up")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read()-degree,elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()); // min 15
      } else if (orientation.equals("down")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read()+degree,elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()); // max 165
      } else {
        // Only used if necessary for debugging
      }
      break;
      
    case 'e':
      // To identify
      break;
      
    case 'u':
      // To identify
      break;
      
    case 'w':
      // To identify
      break;
      
    case 'g':
      if (orientation.equalsIgnoreCase("open")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()+degree); // max 73
      } else if (orientation.equals("down")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()-degree); // min 10
      } else {
        // Only used if necessary for debugging
      }
      break;
      
    default: 
      currentPosition();
  }
}
*/

