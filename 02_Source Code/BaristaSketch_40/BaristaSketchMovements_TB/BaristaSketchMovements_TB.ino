#include <SoftwareSerial.h>
#include <Braccio.h>
#include <Servo.h>
#include "Wire.h"
#include "Adafruit_TCS34725.h"

// Instantiate object
Servo base;
Servo shoulder;
Servo elbow;
Servo wrist_ver;
Servo wrist_rot;
Servo gripper;

<<<<<<< HEAD
int forearm=wrist_ver.read()-180;
int wrist=wrist_rot.read()+180;

// Instantiate object
int bluetoothTx = 2;  // TX-O pin of bluetooth mate, Arduino D2
int bluetoothRx = 3;  // RX-I pin of bluetooth mate, Arduino D3
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);

// Instantiate object
Adafruit_TCS34725 tcs = Adafruit_TCS34725(TCS34725_INTEGRATIONTIME_50MS, TCS34725_GAIN_1X);

// Define Standard position
=======
// Standard position
>>>>>>> 78592a95c7d6dc57c7ef7a841c838dbd6f403ffb
const int ba = 95;
const int sh = 90;
const int el = 140;
const int wrV = 180;
const int wrR = 65;
const int gr = 10;

// Define known drinks in array
const int colourArraySize = 4; 
const String colours[colourArraySize] = {"tomato", "blueberry", "water", "fanta"}; // Insert new drink
 
void setup()
{
  // Starts Braccio
  Braccio.begin();

  // Open data rate an sets serial port to 9600 bps
  Serial.begin(9600);   
  bluetooth.begin(9600);

  // Check if color sensor is returning data
  if (tcs.begin()) {
    Serial.println("Sensor found");
  } else {
    Serial.println("TCS34725 not gefunden ... Process stopped!");
    while (1)
    delay(100); // Wait!
  } 
}

void loop(){
  // Send data only when you receive data
  if (bluetooth.available()){

    // Assigns the received string from the android device
    String btString = bluetooth.readString();
    Serial.println(btString);
    int btStringInt = btString.toInt();  // Only used for patterns

    // Prepares string for further usage
    btString.trim(); // Important for String comparison 
    btString.toLowerCase();

    // Initializes values to cut substrings for absolute and relative movements 
    String btJointString = "";
    int btDegree = 0;
    String btOrientation = "";

    // Check for drink commands 
    for (int i = 0; i < colourArraySize; i++){
      if (btString.equalsIgnoreCase(colours[i])){
        Serial.println("Braccio is searching for: " + btString);
        searchBeverage(btString);
        break;
      }
    }

    // Check for pattern commands that are sent with numbers
    switch(btStringInt){
      case 1:
        helloPattern();
        Serial.println("Braccio is performing Pattern Hello(1)!");
        break;
        
      case 2:
        goodbyePattern();
        Serial.println("Braccio is performing Pattern Goodbye(2)!");
        break;
        
      case 3:
        grapPattern();
        Serial.println("Braccio is performing Pattern Grap(3)!");
        break;
        
      case 4:
        releasePattern();
        Serial.println("Braccio is performing Pattern Release(4)!");
        break;
        
      case 5:
        standardPattern();
        Serial.println("Braccio is performing Pattern Standard(5)!");
        break;

      case 6:
        stretchPattern();
        Serial.println("Braccio is performing Pattern Stretch(6)!");
        break;
        
      case 7:
        crocodilePattern();
        Serial.println("Braccio is performing Pattern Crocodile(7)!");
        break;

      case 8:
        startPattern(0,10);
        Serial.println("Braccio is performing Pattern Start(8)!");
        break;
        
      case 9:
        String color = checkColour();
        Serial.println("Braccio is performing Pattern CheckColor(9)! " + color + " drink found");
        break;
    }

    // Check for move commands; Commands are either in the form "base_90/right." or "base_90."
    if (btString.indexOf("_") > 0){
      btJointString = btString.substring(0, btString.indexOf("_"));

      if(btString.indexOf("/") > 0){
        btDegree = btString.substring(btString.indexOf("_")+1, btString.indexOf("/")).toInt();
        btOrientation = btString.substring(btString.indexOf("/")+1, btString.indexOf("."));
        Serial.print("The Joint " + btJointString + " is chosen. It turns to the relative " + btOrientation + "position degree ");
        Serial.println(btDegree);
        singleCommandRelative(btJointString, btOrientation, btDegree);  
        
      } else if (btString.indexOf(".") > 0){  
        btDegree = btString.substring(btString.indexOf("_")+1, btString.indexOf(".")).toInt();
        Serial.print("The Joint " + btJointString + " is chosen. It turns to the absolute position degree ");
        Serial.println(btDegree);
        singleCommandAbsolute(btJointString, btDegree);         
      }  
    }
  }

  // Returns console content to bluetooth
  if (Serial.available()) // Serial input
  {
    bluetooth.print((char)Serial.read()); 
  }
}

/* MOVE SINGLE JOINTS */
// Is used to apply absolute joint changes
void singleCommandAbsolute (String joint, int degree){
  // Prints the current position to the console
  Serial.println(joint);
  Serial.println(degree);
  Serial.println(base.read());
  Serial.println(shoulder.read());
  Serial.println(elbow.read());
  Serial.println("--------");
  Serial.println(wrist_ver.read());
  Serial.println(wrist_rot.read());
  Serial.println(gripper.read());

  // Checks for the addressed
  if (joint.equalsIgnoreCase("base")){
<<<<<<< HEAD
    Braccio.ServoMovement (30,degree,shoulder.read(),elbow.read(), wrist_ver.read(), wrist_rot.read(), gripper.read());
  } else if (joint.equalsIgnoreCase("shoulder")){
    Braccio.ServoMovement (30,base.read(),degree,elbow.read(), wrist_ver.read(), wrist_rot.read(), gripper.read());
  } else if (joint.equalsIgnoreCase("elbow")){
    Braccio.ServoMovement (30,base.read(),shoulder.read(),degree, wrist_ver.read(), wrist_rot.read(), gripper.read());
  } else if (joint.equalsIgnoreCase("forearm")){
    Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(), degree, wrist_rot.read(), gripper.read());
  } else if (joint.equalsIgnoreCase("wrist")){
    Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(), wrist_ver.read(),degree, gripper.read());
  } else if (joint.equalsIgnoreCase("gripper")){
    Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(), wrist_ver.read(), wrist_rot.read(), degree);
=======
    Braccio.ServoMovement (30,degree,shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read());
    Serial.println("sCA base");
    Serial.println(wrist_ver.read());
  Serial.println(wrist_rot.read());
  } else if (joint.equalsIgnoreCase("shoulder")){
    Braccio.ServoMovement (30,base.read(),degree,elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read());
    Serial.println("sCA shoulder");
  } else if (joint.equalsIgnoreCase("elbow")){
    Braccio.ServoMovement (30,base.read(),shoulder.read(),degree,wrist_ver.read(),wrist_rot.read(),gripper.read());
    Serial.println("sCA elbow");
  } else if (joint.equalsIgnoreCase("forearm")){
    Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),degree,wrist_rot.read(),gripper.read());
    Serial.println("sCA forearm");
  } else if (joint.equalsIgnoreCase("wrist")){
    Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),degree,gripper.read());
    Serial.println("sCA wrist");
  } else if (joint.equalsIgnoreCase("gripper")){
    Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),degree);
    Serial.println("sCA gripper");
>>>>>>> 78592a95c7d6dc57c7ef7a841c838dbd6f403ffb
  } else {
    currentPosition();
    Serial.println("No command matched in singleCommandAbsolute method!");
  }
}

// Is used to apply relative joint changes
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
<<<<<<< HEAD
        Braccio.ServoMovement (30,base.read()+degree,shoulder.read(),elbow.read(), wrist_ver.read(), wrist_rot.read(), gripper.read()); // max 180               
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read()-degree,shoulder.read(),elbow.read(), wrist_ver.read(), wrist_rot.read(), gripper.read()); // min 0
=======
        Braccio.ServoMovement (30,base.read()+degree,shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()); // max 180               
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read()-degree,shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()); // min 0
>>>>>>> 78592a95c7d6dc57c7ef7a841c838dbd6f403ffb
      } else {
        // Only used if necessary for debugging
      }
  } else if (joint.equalsIgnoreCase("shoulder")){
    if (orientation.equalsIgnoreCase("right")) {
<<<<<<< HEAD
        Braccio.ServoMovement (30,base.read(),shoulder.read()-degree,elbow.read(), wrist_ver.read(), wrist_rot.read(), gripper.read()); // min 15
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read()+degree,elbow.read(), wrist_ver.read(), wrist_rot.read(), gripper.read()); // max 165
=======
        Braccio.ServoMovement (30,base.read(),shoulder.read()-degree,elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()); // min 15
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read()+degree,elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()); // max 165
>>>>>>> 78592a95c7d6dc57c7ef7a841c838dbd6f403ffb
      } else {
        // Only used if necessary for debugging
      }
  } else if (joint.equalsIgnoreCase("elbow")){
    if (orientation.equalsIgnoreCase("right")) {
<<<<<<< HEAD
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read()-degree, wrist_ver.read(), wrist_rot.read(), gripper.read()); // min 15
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read()+degree, wrist_ver.read(), wrist_rot.read(), gripper.read()); // max 165
      } else {
        // Only used if necessary for debugging
      }
  } else if (joint.equalsIgnoreCase("forearm")){
    if (orientation.equalsIgnoreCase("right")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(), wrist_ver.read()-degree, wrist_rot.read(), gripper.read()); // min 15
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(), wrist_ver.read()+degree, wrist_rot.read(), gripper.read()); // max 165
      } else {
        // Only used if necessary for debugging
      }
  } else if (joint.equalsIgnoreCase("wrist")){
    if (orientation.equalsIgnoreCase("right")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(), wrist_ver.read(), wrist_rot.read()-degree, gripper.read()); // max 180      
        wrist=wrist-degree;         
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(), wrist_ver.read(), wrist_rot.read()+degree, gripper.read()); // min 0
        wrist=wrist+degree;
=======
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read()-degree,wrist_ver.read(),wrist_rot.read(),gripper.read()); // min 15
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read()+degree,wrist_ver.read(),wrist_rot.read(),gripper.read()); // max 165
      } else {
        // Only used if necessary for debugging
      }

  } else if (joint.equalsIgnoreCase("forearm")){
    if (orientation.equalsIgnoreCase("right")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read()-degree,wrist_rot.read(),gripper.read()); // min 15
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read()+degree,wrist_rot.read(),gripper.read()); // max 165
      } else {
        // Only used if necessary for debugging
      }

  } else if (joint.equalsIgnoreCase("wrist")){
    if (orientation.equalsIgnoreCase("right")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read()-degree,gripper.read()); // max 180      
      } else if (orientation.equals("left")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read()+degree,gripper.read()); // min 0
>>>>>>> 78592a95c7d6dc57c7ef7a841c838dbd6f403ffb
      } else {
        // Only used if necessary for debugging
      }
  } else if (joint.equalsIgnoreCase("gripper")){
    if (orientation.equalsIgnoreCase("open")) {
<<<<<<< HEAD
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(), wrist_ver.read(), wrist_rot.read(), gripper.read()+degree); // max 73
      } else if (orientation.equals("down")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(), wrist_ver.read(), wrist_rot.read(), gripper.read()-degree); // min 10
=======
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()+degree); // max 73
      } else if (orientation.equals("down")) {
        Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()-degree); // min 10
>>>>>>> 78592a95c7d6dc57c7ef7a841c838dbd6f403ffb
      } else {
        // Only used if necessary for debugging
      }
  } else {
    currentPosition();
    Serial.println("No command matched in singleCommandRelative!");
  }
}

/* SEARCH DRINKS */
// Senses the color in front of the sensor. THe easiest ones are Red, Green and Blue
String checkColour (){
   // The sensor returns a value for R, G, B and a clear value
   uint16_t clearcol, red, green, blue;
   float average, r, g, b;
   String colour = "";
   delay(100); // Color sensing takes about 50 ms
   tcs.getRawData(&red, &green, &blue, &clearcol);
   
   // Calcualte Average from RGB
   average = (red+green+blue)/3;
   // Calculate colors divided by average to get values around 1.0 
   r = red/average;
   g = green/average;
   b = blue/average;
   // Print clear value and r, g, b serialized to the console
   Serial.print("\tClear:"); Serial.print(clearcol);
   Serial.print("\tRed:"); Serial.print(r);
   Serial.print("\tGreen:"); Serial.print(g);
   Serial.print("\tBlue:"); Serial.print(b);
   
   // Color detection. Adapt for different outcomes
   if ((r > 1.4) && (g < 0.9) && (b < 0.9)) {
    Serial.println("\tRed");
    colour = "tomato";
    return colour;
<<<<<<< HEAD
   } else if ((r > 0.5) && (r < 0.8) && (g > 0.95) && (g < 1.05) && (b > 1.1) && (b < 0.9)) { // r < 0.8 && g< 1.2
    Serial.println("\tBlue");
=======
   } else if ((r > 0.5) && (r < 1) && (g > 0.95) && (g < 1.05) && (b > 0.9) && (b < 1.4)) { // r < 0.8 && g< 1.2
    Serial.println("\tBLAU");
>>>>>>> 78592a95c7d6dc57c7ef7a841c838dbd6f403ffb
    colour = "blueberry";
    return colour;
   }
   else if ((r > 1.25) && (r < 1.35) && (g > 1.05) && (g < 1.15) && (b > 0.55) && (b < 0.65)) {
    Serial.println("\tYellow");
    colour = "fanta";
    return colour;
   }
   else if ((r > 0.95) && (r < 1.1) && (g > 1.0) && (g < 1.1) && (b > 0.85) && (b < 1.0)) {
    Serial.println("\tWHITE");
    colour = "water";
    return colour;
   } 
   else {
    Serial.println("\tNo color detected!"); 
    colour = "no";
    return colour;
   }
}

// Performs a predefined pattern to search a drink within preset positions 
void searchBeverage (String btColour) {
    int moveBase = 0;
    // Brings the arm to the startPosition
    startPattern(moveBase,10);

    // Checking the different positions. For more positions change the i in the for loop
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

/* PATTERNS */
void currentPosition (){
  Braccio.ServoMovement (30,base.read(),shoulder.read(),elbow.read(),wrist_ver.read(),wrist_rot.read(),gripper.read()); //current position
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
    Braccio.ServoMovement (20,90,90,180,135,90,73);
    delay(2000);
    Braccio.ServoMovement (20,90,90,90,135,90,73);
    Braccio.ServoMovement (20,140,90,180,135,90,73);
    delay(2000);
    Braccio.ServoMovement (20,140,90,90,135,90,73);
    Braccio.ServoMovement (20,90,90,90,135,90,73);
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

// This pattern exists for test purposes to cosntantly check the color
void colourLoop(){
  scanSidePattern(80,10);
  while(1){
  checkColour();
  delay(500);
  }
}
