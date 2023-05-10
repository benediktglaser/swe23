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

#define NAME_PREFIX "Station G1T2"
#define PAIRING_TIMEOUT 60*5
#define BLE_BUTTON D2
#define ERROR_BUTTON D3

#define NUM_SAMPLES 5

typedef struct {
  float temperature;
  uint32_t pressure;
  float humidity;
  uint32_t air_quality;
} bmeData_struct;

typedef struct {
  double temperature;
  double pressure;
  double humidity;
  double air_quality;
  double soil;
  double light;
} data_struct;

Adafruit_BME680 bme;
char name[255] = {0};
int id;
int iteration = 0;
data_struct data[NUM_SAMPLES] = {{0}};
data_struct mean_data = {0};
// timestamp for reading data
unsigned long timestamp_data = millis();
// timestamp for error light
unsigned long timestamp_error = millis();
bool gardener_is_here = false;
bool active_excess = false;
unsigned int excess = 1;
char error_uuid[64] = {0};
char data_types[6][16] = {"temp", "pressure", "air_quality", "humid", "soil", "light"};
char temp_limit_uuid[64] = "0x4102AA";
char pressure_limit_uuid[64] = "0x4102BB";
char humid_limit_uuid[64] = "0x4102CC";
char quality_limit_uuid[64] = "0x4102DD";
char soil_limit_uuid[64] = "0x4102EE";
char light_limit_uuid[64] = "0x4102FF";


int readDip();
int readSoil();
int readLight();
bmeData_struct readBME688();
void printData(data_struct data);
data_struct readData();
void setColor(int red, int green, int blue);
data_struct calculateMeanData(data_struct* data);
void setErrorLight();

// BLE forward declarations

void ble_connect();
void setSensorDataInBLE(data_struct data);

  // Service and Characteristics

BLEService sensorDataService("0x4102");
BLEDoubleCharacteristic temperature("0x4102A0", BLERead);
BLEDoubleCharacteristic pressure("0x4102B0", BLERead);
BLEDoubleCharacteristic humidity("0x4102C0", BLERead);
BLEDoubleCharacteristic airQuality("0x4102D0", BLERead);
BLEDoubleCharacteristic soil("0x4102E0", BLERead);
BLEDoubleCharacteristic light("0x4102F0", BLERead);
BLEIntCharacteristic temperatureLimit("0x4102AA", BLERead | BLEWrite);
BLEIntCharacteristic pressureLimit("0x4102BB", BLERead | BLEWrite);
BLEIntCharacteristic humidityLimit("0x4102CC", BLERead | BLEWrite);
BLEIntCharacteristic airQualityLimit("0x4102DD", BLERead | BLEWrite);
BLEIntCharacteristic soilLimit("0x4102EE", BLERead | BLEWrite);
BLEIntCharacteristic lightLimit("0x4102FF", BLERead | BLEWrite);
BLEBoolCharacteristic gardener("0x410290", BLERead);

  // Eventhandlers
void blePeripheralConnectHandler(BLEDevice central);
void blePeripheralDisconnectHandler(BLEDevice central);
void writeLimitHandler(BLEDevice central, BLECharacteristic characteristic);
void readGardenerHandler(BLEDevice central, BLECharacteristic characteristic);

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

  sprintf(name, "%s %03d", NAME_PREFIX, id);
  BLE.setLocalName(name);
  BLE.setDeviceName(name);

  BLE.setEventHandler(BLEConnected, blePeripheralConnectHandler);
  BLE.setEventHandler(BLEDisconnected, blePeripheralDisconnectHandler);

  temperatureLimit.setEventHandler(BLERead, writeLimitHandler);
  pressureLimit.setEventHandler(BLERead, writeLimitHandler);
  humidityLimit.setEventHandler(BLERead, writeLimitHandler);
  airQualityLimit.setEventHandler(BLERead, writeLimitHandler);
  soilLimit.setEventHandler(BLERead, writeLimitHandler);
  lightLimit.setEventHandler(BLERead, writeLimitHandler);
  gardener.setEventHandler(BLERead, readGardenerHandler);


  sensorDataService.addCharacteristic(temperature);
  sensorDataService.addCharacteristic(pressure);
  sensorDataService.addCharacteristic(humidity);
  sensorDataService.addCharacteristic(airQuality);
  sensorDataService.addCharacteristic(soil);
  sensorDataService.addCharacteristic(light);
  sensorDataService.addCharacteristic(temperatureLimit);
  sensorDataService.addCharacteristic(pressureLimit);
  sensorDataService.addCharacteristic(humidityLimit);
  sensorDataService.addCharacteristic(airQualityLimit);
  sensorDataService.addCharacteristic(soilLimit);
  sensorDataService.addCharacteristic(lightLimit);
  sensorDataService.addCharacteristic(gardener);

  BLE.addService(sensorDataService);

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
  static bool error_light_is_on = false;
  bool establish_connection = false;
  if (!digitalRead(BLE_BUTTON)) {
    establish_connection = true;
  }
  if (!BLE.connected() && establish_connection) {
    Serial.println("Connecting");
    ble_connect();
  }
  
  BLE.poll();

// select samples every 2000ms
  if (millis() - timestamp_data > 2000) {
    data[iteration] = readData();
    iteration++;
    timestamp_data = millis();
  }

// if NUM_SAMPLES samples have been collected, send mean of samples
if (iteration == NUM_SAMPLES) {
  iteration = 0;
  mean_data = calculateMeanData(data);
  setSensorDataInBLE(mean_data);
  printData(mean_data);
}  
  // TODO: set up a loop here that
  // 1. meassures the data
  // 2. sends the data and
  // 3. checks the limit of the recieved data and if necessary, calls the errorLight()-function.
  // The only way to clear this light is to press the button on D2. If the value is still to
  // low/high, the light should light up again. If the button on D3 is pressed, the loop should
  // be left and the pairing-mode should start.

  if (active_excess) {
    if (millis() - timestamp_error > 50000 / excess) {
      if (error_light_is_on) {
        setColor(0, 0, 0); 
      } else {
        setErrorLight();
      }
      timestamp_error = millis();
      error_light_is_on = !error_light_is_on;
    }
  }
  if (active_excess && !digitalRead(ERROR_BUTTON)) {
    active_excess = false;
    gardener_is_here = true;
    setColor(0, 0, 0);
  }
  gardener.setValue(gardener_is_here);
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
happen to later enable the calculation of the mean in the collectData() function.
<time_delay> must be larger than 2000.
*/
data_struct readData(){
  data_struct data = {0, 0, 0, 0, 0, 0};
  bmeData_struct bmeData = readBME688();
  data.air_quality = (float) bmeData.air_quality;
  data.humidity = bmeData.humidity;
  data.pressure = (float) bmeData.pressure;
  data.temperature = bmeData.temperature;
  data.soil = (float) readSoil();
  data.light = (float) readLight();
  return data;
}

data_struct calculateMeanData(data_struct* data) {
  data_struct mean = {0};
  for (int i = 0; i < NUM_SAMPLES; i++) {
    mean.air_quality += data[i].air_quality;
    mean.humidity += data[i].humidity;
    mean.light += data[i].light;
    mean.pressure += data[i].pressure;
    mean.soil += data[i].soil;
    mean.temperature += data[i].temperature;
  }

  mean.air_quality = mean.air_quality / NUM_SAMPLES;
  mean.humidity = mean.humidity / NUM_SAMPLES;
  mean.light = mean.light / NUM_SAMPLES;
  mean.pressure = mean.pressure / NUM_SAMPLES;
  mean.soil = mean.soil / NUM_SAMPLES;
  mean.temperature = mean.temperature / NUM_SAMPLES;

  return mean;
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
  BLE.advertise();
  time_t start_time = time(NULL);
  unsigned long timestamp = 0;
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

void setSensorDataInBLE(data_struct data) {
  temperature.writeValue(data.temperature);
  pressure.writeValue(data.pressure);
  humidity.writeValue(data.humidity);
  airQuality.writeValue(data.air_quality);
  soil.writeValue(data.soil);
  light.writeValue(data.light);
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

void writeLimitHandler(BLEDevice central, BLECharacteristic characteristic) {
  Serial.print("Characteristic event written; new value: ");
  strcpy(error_uuid, characteristic.uuid());
  const int *value = (int *) characteristic.value();
  // only allow values between 20% and 200% so that light interval is bearable
  excess = value[0] < 20 ? 20 : value[0] > 200 ? 200 : value[0];
  Serial.println(excess);
  active_excess = true;
}

void readGardenerHandler(BLEDevice central, BLECharacteristic characteristic) {
  Serial.println("Gardener-Value read");
  gardener_is_here = false;
}

/*
This function provides error-codes via the RBG-LED. Different parameters
(error_uuid) have different colors.
*/
void setErrorLight() {
  if(strcmp(error_uuid, temp_limit_uuid) == 0) {
    setColor(255, 0, 0);
  } else if(strncmp(error_uuid, pressure_limit_uuid, 8) == 0) {
    setColor(0, 255, 0);
  } else if(strncmp(error_uuid, quality_limit_uuid, 8) == 0) {
    setColor(0, 0, 255);
  } else if(strncmp(error_uuid, humid_limit_uuid, 8) == 0) {
    setColor(255, 255, 0);
  } else if(strncmp(error_uuid, soil_limit_uuid, 8) == 0) {
    setColor(0, 255, 255);
  } else if(strncmp(error_uuid, light_limit_uuid, 8) == 0) {
    setColor(255, 0, 255);
  }
}