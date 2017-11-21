#include <Braccio.h>
#include <Servo.h>

Servo base;
Servo shoulder;
Servo elbow;
Servo wrist_ver;
Servo wrist_rot;
Servo gripper;

char a;

void setup() {

 Braccio.begin();
 bewegungsEingabe();

} 

void loop() {
  
}

void bewegungsEingabe() 
{

 Serial.begin(9600);
 Serial.println ("Welche Richtung?"); 

 
 while (Serial.available() == 0){}
  
  a = Serial.read();

  Serial.println(a);

      
  if (a=='r')
  {
       vorne();  
       }
  else
  { 
      hinten();
    }
}
  

void vorne() {
    
  Braccio.ServoMovement (20,90,30,30,45,70,10);

}

void hinten() {

  Braccio.ServoMovement (20,90,180,30,45,70,73);
 
}


