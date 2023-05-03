#include <Arduino.h>
#include <Wire.h>
#include <SPI.h>
#include <Adafruit_Sensor.h>
#include "Adafruit_BME680.h"
#include <ArduinoBLE.h>
#include <stdio.h>
#include <time.h>
#include <string.h>

#define BME_SCK 13
#define BME_MISO 12
#define BME_MOSI 11
#define BME_CS 10

#define SEALEVELPRESSURE_HPA (1013.25)

#define NAME_PREFIX "SensorStation G1T2"
#define PAIRING_TIMEOUT 60*5

Adafruit_BME680 bme;
int id;

typedef struct {
  float temperature;
  uint32_t pressure;
  float humidity;
  uint32_t air_quality;
} bmeData_struct;

typedef struct {
  float temperature;
  float pressure;
  float humidity;
  float air_quality;
  float soil;
  float light;
} data_struct;

int readDip();
int readSoil();
int readLight();
bmeData_struct readBME688();
void printData(data_struct data);
data_struct readData(int time_delay);
void setColor(int red, int green, int blue);
data_struct collectData(int samples, int time_delay);

// BLE forward declarations

void blePeripheralConnectHandler(BLEDevice central);
void blePeripheralDisconnectHandler(BLEDevice central);
void ble_connect();

void setup() {
  Serial.begin(9600);
  pinMode(A0, OUTPUT);          //buzzer

  pinMode(A1, OUTPUT);          //red
  pinMode(A2, OUTPUT);          //blue
  pinMode(A3, OUTPUT);          //green

  pinMode(D2, INPUT_PULLUP);    //outer button
  pinMode(D3, INPUT_PULLUP);    //inner button

  pinMode(D5, INPUT);  //dip switch MSB
  pinMode(D6, INPUT);  //dip switch
  pinMode(D7, INPUT);  //dip switch
  pinMode(D8, INPUT);  //dip switch
  pinMode(D9, INPUT);  //dip switch
  pinMode(D10, INPUT); //dip switch
  pinMode(D11, INPUT); //dip switch
  pinMode(D12, INPUT); //dip switch LSB

  if (!bme.begin()) {
    Serial.println(F("Could not find a valid BME680 sensor, check wiring!"));
    while (1);
  }
  // Set up oversampling and filter initialization
  bme.setTemperatureOversampling(BME680_OS_8X);
  bme.setHumidityOversampling(BME680_OS_2X);
  bme.setPressureOversampling(BME680_OS_4X);
  bme.setIIRFilterSize(BME680_FILTER_SIZE_3);
  bme.setGasHeater(320, 150); // 320*C for 150 ms

  id = readDip();

  if (!BLE.begin()) {
    Serial.println("Starting BLE failed!");

    while(1);
  }
  Serial.println("BLE startet");

  Serial.print("Setup, complete, ID is ");
  Serial.println(id);
  Serial.println("==========================");

  // startup sound
  tone(A0, 257, 200); // C
  delay(200);
  tone(A0, 323, 200); // E
  delay(200);
  tone(A0, 385, 200); // G

}

void loop() {
  bool establish_connection = false;
  if (!digitalRead(D2)) {
    establish_connection = true;
  }
  if (!BLE.connected() && establish_connection) {
    Serial.println("Connecting");
    ble_connect();
  }
  // I have to call this every time before poll
  // otherwise it still advertises
  BLE.stopAdvertise();
  BLE.poll();
  // TODO: set up a loop here that
  // 1. meassures the data
  // 2. sends the data and
  // 3. checks the limit of the recieved data and if necessary, calls the errorLight()-function.
  // The only way to clear this light is to press the button on D2. If the value is still to
  // low/high, the light should light up again. If the button on D3 is pressed, the loop should
  // be left and the pairing-mode should start.
}

/*
Sets a color to the RGB-LED
*/
void setColor(int red, int green, int blue)
{
  analogWrite(A1, red);
  analogWrite(A2, blue);  
  analogWrite(A3, green);
}

/*
Reads the soil moisture sensor.
*/
int readSoil(){
  return analogRead(A6);
}

/*
Reads the light sensor.
*/
int readLight(){
  return analogRead(A7);
}

/*
Reads and returns the BME688-sensor. These are temperature, pressure,
humidity, gas resistance and altitude. Between reads you must
wait >= 2000ms. This is ensured in readData(int time_delay).
*/
bmeData_struct readBME688(){
  bmeData_struct bmeData = {0, 0, 0, 0};
  if (!bme.beginReading()) {
    Serial.println(F("Failed to begin reading :("));
    return bmeData;
  }
  if (!bme.endReading()) {
    Serial.println(F("Failed to complete reading :("));
    return bmeData;
  }

  bmeData.temperature = bme.temperature;                     //temperature [°C]
  bmeData.pressure = bme.pressure / 100.0;                   //pressure [hPa]
  bmeData.humidity = bme.humidity;                           //humidity [%]
  bmeData.air_quality = bme.gas_resistance / 1000.0;      //gas [KOhms]
  
  return bmeData;
}

/*
Reads data from all sensors and then waits for <time_delay> ms. The casts to float
happen to later enable the calculation of the average in the collectData() function.
<time_delay> must be larger than 2000.
*/
data_struct readData(int time_delay){
  data_struct data = {0, 0, 0, 0, 0, 0};
  if(time_delay < 2000){
    Serial.print("time_delay for data_struct readData(int time_delay) must be >= 2000, is ");
    Serial.print(time_delay);
    return data;
  }
  bmeData_struct bmeData = readBME688();
  data.air_quality = (float) bmeData.air_quality;
  data.humidity = bmeData.humidity;
  data.pressure = (float) bmeData.pressure;
  data.temperature = bmeData.temperature;
  data.soil = (float) readSoil();
  data.light = (float) readLight();
  delay(time_delay);
  return data;
}

/*
Reads the dip-switch to calculate the id for this Sensor-Station.
D5 is the MSB, D12 the LSB. Returns a decimal value.
*/
int readDip(){
  int dipSwitchPins[] = {D5, D6, D7, D8, D9, D10, D11, D12};
  int dipSwitchValues[] = {1, 2, 4, 8, 16, 32, 64, 128};
  int decimalValue = 0;
  for (int i = 0; i < 8; i++) {
    if (digitalRead(dipSwitchPins[i])) {
        decimalValue += dipSwitchValues[i];
    }
  }
  return decimalValue;
}

/*
Takes <samples> data samples and waits <time_delay> ms between
each of them. The aritmetic average is calculated for all of
them and then returned. <time_delay> must be larger than 2000.
*/
data_struct collectData(int samples, int time_delay){
  data_struct average = {0, 0, 0, 0, 0, 0};
  data_struct data;
  for(int i = 0; i < samples; i++){
    data = readData(time_delay);
    average.air_quality += data.air_quality;
    average.humidity += data.humidity;
    average.light += data.light;
    average.pressure += data.pressure;
    average.soil += data.soil;
    average.temperature += data.temperature;
  }

  average.air_quality = average.air_quality / samples;
  average.humidity = average.humidity / samples;
  average.light = average.light / samples;
  average.pressure = average.pressure / samples;
  average.soil = average.soil / samples;
  average.temperature = average.temperature / samples;

  return average;
}

/*
For debugging purposes only. Prints out all data of a data_struct.
*/
void printData(data_struct data){
Serial.println();
  Serial.print("Pressure: ");
  Serial.print(data.pressure);
  Serial.println("hPa");
  Serial.print("Humidity: ");
  Serial.print(data.humidity);
  Serial.println("%");
  Serial.print("Gas: ");
  Serial.print(data.air_quality);
  Serial.println("KOhm");
  Serial.print("Temperature: ");
  Serial.print(data.temperature);
  Serial.println("°C");
  Serial.print("Light: ");
  Serial.println(data.light);
  Serial.print("Soil: ");
  Serial.println(data.soil);
  Serial.println("============");
}

void ble_connect() {
  char name[255] = {0};
  sprintf(name, "%s %d", NAME_PREFIX, id);
  BLE.setLocalName(name);
  BLE.setDeviceName(name);

  BLE.setEventHandler(BLEConnected, blePeripheralConnectHandler);
  BLE.setEventHandler(BLEDisconnected, blePeripheralDisconnectHandler);

  BLE.advertise();
  time_t start_time = time(NULL);
  long timestamp = 0;
  while(difftime(time(NULL), start_time) < PAIRING_TIMEOUT) {
    if(BLE.connected()) {
      break; 
    }
    // according to https://tigoe.github.io/BluetoothLE-Examples/ArduinoBLE_library_examples/
    // it's better to do these if-clauses instead of delay()
    // so that BLE.poll() isn't delayed (might lose connection otherwise)
    if(millis() - timestamp > 100) {
      setColor(0, 0, 150);
    }
    if(millis() - timestamp > 200) {
      setColor(0, 0, 0);
      timestamp = millis();
    }
    BLE.poll();
  }
  setColor(0, 0, 0);
  if(!BLE.connected()) {
    // timeout sound
    tone(A0, 514, 200); // C
    delay(200);
    tone(A0, 385, 200); // G
    delay(200);
    tone(A0, 323, 200); // E
    delay(200);
    tone(A0, 257, 200); // C
  }
  Serial.println("Stop advertising");
}

void blePeripheralConnectHandler(BLEDevice central) {
  BLE.stopAdvertise();
  Serial.println("Connected event, central: ");
  Serial.println(central.address());
  // connection sound
  tone(A0, 323, 200); // E
  delay(200);
  tone(A0, 385, 200); // G
  delay(200);
  tone(A0, 514, 200); // C
}

void blePeripheralDisconnectHandler(BLEDevice central) {
  Serial.println("Disconnected event, central: ");
  Serial.println(central.address());
}

/*
This function provides error-codes via the RBG-LED. Different parameters
(type) have different colors. The larger the error (limit) the faster the
LED blinks. The number of blinks can be set using count.
*/
void errorLight(char* type, float limit, int count){
  if(strcmp(type, "temp") == 0) {
    for(int i = 0; i < count; i++) {
      setColor(255, 0, 0);
      delay(1/limit);
      setColor(0, 0, 0);
      delay(1/limit);
    }
  } else if(strcmp(type, "pressure") == 0){
    for(int i = 0; i < count; i++){
      setColor(0, 255, 0);
      delay(1/limit);
      setColor(0, 0, 0);
      delay(1/limit);
    }
  } else if(strcmp(type, "air_quality") == 0){
    for(int i = 0; i < count; i++){
      setColor(0, 0, 255);
      delay(1/limit);
      setColor(0, 0, 0);
      delay(1/limit);
    }
  } else if(strcmp(type, "humid") == 0){
    for(int i = 0; i < count; i++){
      setColor(255, 255, 0);
      delay(1/limit);
      setColor(0, 0, 0);
      delay(1/limit);
    }
  } else if(strcmp(type, "soil") == 0){
    for(int i = 0; i < count; i++){
      setColor(0, 255, 255);
      delay(1/limit);
      setColor(0, 0, 0);
      delay(1/limit);
    }
  } else if(strcmp(type, "light") == 0){
    for(int i = 0; i < count; i++){
      setColor(255, 0, 255);
      delay(1/limit);
      setColor(0, 0, 0);
      delay(1/limit);
    }
  }
}