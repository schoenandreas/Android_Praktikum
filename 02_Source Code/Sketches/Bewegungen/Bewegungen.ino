#include <Braccio.h>
#include <Servo.h>

Servo base;
Servo shoulder;
Servo elbow;
Servo wrist_ver;
Servo wrist_rot;
Servo gripper;

char eingb;

void setup() {

 Braccio.begin();
 //saghallo();
} 

void loop() {

 eingabe();
 positionUno();
 positionDue();
 positionTre();
 positionQuattro();
 positionCinque();
 exception();

 Braccio.ServoMovement(20,90,90,180,180,90,10); //safety position
  
  
}

void saghallo()
{
  Braccio.ServoMovement (20,80,90,90,45,60,73);
  Braccio.ServoMovement (20,110,90,90,45,180,10);
  Braccio.ServoMovement (20,80,90,90,45,60,73);
  Braccio.ServoMovement (20,110,90,90,45,180,10);
}

void positionUno() {
  
  Braccio.ServoMovement(20,0,30,90,30,90,10); 
  delay (1000);
  if (eingb == '1'){
  Braccio.ServoMovement(20,0,20,60,30,40,73);
  Braccio.ServoMovement(20,0,50,120,50,110,73);
  passt();}
  else {
  Braccio.ServoMovement(20,0,30,90,30,90,10);}
}

void positionDue() {

   Braccio.ServoMovement(20,30,30,90,30,90,10); 
  delay (1000);
  if (eingb == '2'){
  Braccio.ServoMovement(20,30,20,60,30,40,73);
  Braccio.ServoMovement(20,30,50,120,50,110,73);
  passt();}
  else{
  Braccio.ServoMovement(20,30,30,90,30,90,10);}
}

void positionTre() {

   Braccio.ServoMovement(20,60,30,90,30,90,10); 
  delay (1000);
  if (eingb == '3'){
  Braccio.ServoMovement(20,60,20,60,30,40,73);
  Braccio.ServoMovement(20,60,50,120,50,110,73);
  passt();}
  else{
  Braccio.ServoMovement(20,60,30,90,30,90,10);}
}

void positionQuattro() {

   Braccio.ServoMovement(20,90,30,90,30,90,10); 
  delay (1000);
  if (eingb == '4'){
  Braccio.ServoMovement(20,90,20,60,30,40,73);
  Braccio.ServoMovement(20,90,50,120,50,110,73);
  passt();}
  else {
  Braccio.ServoMovement(20,90,30,90,30,90,10);}
}

void positionCinque() {
  
 Braccio.ServoMovement(20,120,30,90,30,90,10); 
  delay (1000);
  if (eingb == '5'){
  Braccio.ServoMovement(20,120,20,60,30,40,73);
  Braccio.ServoMovement(20,120,50,120,50,110,73);
  passt();}
  else {
  Braccio.ServoMovement(20,120,30,90,30,90,10);}
}

void exception()
{
  if (eingb == 'e')
  {
    Braccio.ServoMovement(20,90,90,180,180,90,10); //safety position
  }
}

void passt() {

  Braccio.ServoMovement(20,150,50,120,50,110,73); 
  Braccio.ServoMovement(20,150,30,90,30,90,10);
  
  delay (1000);
}

void passtnicht() {

  Braccio.ServoMovement(20,180,30,90,30,90,73); 
  delay (1000);
}

 void eingabe(){
     Serial.begin(9600);
     Serial.println("eingabe?");
     while (Serial.available()==0);
     char a = Serial.read();
     Serial.println(a);

     //vergleiche die Eingabe mit den Positionen. Auf diesen Position muss angegriefen werden. 

     if (a=='1'){
     
       eingb = '1';
     }
     else if (a=='2'){ 
       eingb = '2';
     }
     else if (a=='3')
     {
      eingb = '3';
     }
     else if (a=='4'){
     eingb = '4';
     }
     else if (a=='5')
     {
      eingb = '5';
     }
     else if (a>5)
     {
      eingb = 'e';
     }
     
     
 }
