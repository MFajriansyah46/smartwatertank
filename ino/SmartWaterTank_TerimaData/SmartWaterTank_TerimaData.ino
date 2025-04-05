#include <Arduino.h>
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"
#if defined(ESP32)
  #include <WiFi.h>
#elif defined(ESP8266)
  #include <ESP8266WiFi.h>
#endif

#define API_KEY "AIzaSyCp4RxPe61-wpiuxHivNnlWaNOr2hML-XQ"
#define DATABASE_URL "https://mobiiot-60f89-default-rtdb.asia-southeast1.firebasedatabase.app/"

#define WIFI_SSID "android1"
#define WIFI_PASSWORD "qwertyui"

#define MC_EMAIL "mikrokontroler2@gmail.com"
#define MC_PASSWORD "12345678"

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;
bool loginOK = false;

float toLiter(float cubic){
  return 1000*cubic;
}

void setup() {
  Serial.begin(115200);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");

  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }

  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());

  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  config.token_status_callback = tokenStatusCallback;

  auth.user.email = MC_EMAIL;
  auth.user.password = MC_PASSWORD;
  
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  if (Firebase.ready()) {
    Serial.println("Login successful!");
    Serial.println("");
    loginOK = true;
  } else {
    Serial.println("Login failed!");
  }
}

float height, water_volume;
int pressure;
void loop() {
  Serial.println("Membaca data dari Firebase... ");
  if (Firebase.ready() && loginOK && (millis() - sendDataPrevMillis > 15000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();
    if (Firebase.RTDB.getFloat(&fbdo, "/tb_ukuran/pressure")) {
      if (fbdo.dataType() == "int") {
        pressure = fbdo.intData();
        Serial.print("Tekanan \t");
        Serial.print(pressure);
        Serial.println(" Pa");
        // Serial.println(pressure);
      }
    }
    else {
      Serial.println(fbdo.errorReason());
    }
    if (Firebase.RTDB.getFloat(&fbdo, "/tb_ukuran/height")) {
      if (fbdo.dataType() == "float") {
        height = fbdo.floatData();
        Serial.print("Ketinggian \t");
        Serial.print(height);
        Serial.println(" m");
        // Serial.println(height);
      }
    }
    else {
      Serial.println(fbdo.errorReason());
    }
    if (Firebase.RTDB.getFloat(&fbdo, "/tb_ukuran/water_volume")) {
      if (fbdo.dataType() == "float") {
        water_volume = fbdo.floatData();
        Serial.print("Volume \t\t");
        Serial.print(toLiter(water_volume));
        Serial.println(" L");
      }
    }
    else {
      Serial.println(fbdo.errorReason());
    }
  }
  delay(15000);
}
