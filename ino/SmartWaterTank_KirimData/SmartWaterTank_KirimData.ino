#include <Arduino.h>

#if defined(ESP32)
  #include <WiFi.h>
  #include "time.h"
#elif defined(ESP8266)
  #include <ESP8266WiFi.h>
  #include <time.h>
#endif

#include <Firebase_ESP_Client.h>

// Provide the token generation process info.
#include "addons/TokenHelper.h"
// Provide the Firestore payload printing info and other helper functions.
#include "addons/RTDBHelper.h"

// Insert your network credentials
#define WIFI_SSID "android1"
#define WIFI_PASSWORD "qwertyui"

#define API_KEY "AIzaSyCp4RxPe61-wpiuxHivNnlWaNOr2hML-XQ"
#define DATABASE_URL "https://mobiiot-60f89-default-rtdb.asia-southeast1.firebasedatabase.app/" 
#define FIREBASE_PROJECT_ID "mobiiot-60f89"

#define MC_EMAIL "mikrokontroler1@gmail.com"
#define MC_PASSWORD "12345678"

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;
bool loginOK = false;

float waterVolume(float height) {
  float waterTankDiameter = 1.05;
  float volume = (1.0 / 4.0) * PI * (waterTankDiameter * waterTankDiameter) * height;
  return volume;
}

float pressureToHighValue(float pressure) {
  float maxPressureSet = 13720;
  float minPressureSet = 2940;

  float maxHeightSet = 1.4;
  float minHeightSet = 0.3;

  float height = ((pressure - minPressureSet) / (maxPressureSet - minPressureSet)) * (maxHeightSet - minHeightSet) + minHeightSet;
  return height;
}

String getISO8601Timestamp() {
  time_t now;
  struct tm timeInfo;
  char buffer[30];
  
  time(&now);
  gmtime_r(&now, &timeInfo); // gunakan gmtime_r untuk waktu UTC
  // Format: 2025-03-22T05:39:39Z
  strftime(buffer, sizeof(buffer), "%Y-%m-%dT%H:%M:%SZ", &timeInfo);
  return String(buffer);
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

  configTime(0, 0, "pool.ntp.org", "time.nist.gov");

  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  config.token_status_callback = tokenStatusCallback;

  auth.user.email = MC_EMAIL;
  auth.user.password = MC_PASSWORD;

  Firebase.begin(&config, &auth);

  if (Firebase.ready()) {
    Serial.println("Login successful!");
    Serial.println("");
    loginOK = true;
  } else {
    Serial.println("Login failed!");
  }
}

void loop() {
  // Simulasikan pembacaan sensor
  float pressure = random(1000, 15000);
  float height = pressureToHighValue(pressure);
  float water_volume = waterVolume(height);
  
  if (Firebase.ready() && loginOK && (millis() - sendDataPrevMillis > 15000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();

    // Dapatkan timestamp dalam format ISO 8601
    String timestampStr = getISO8601Timestamp();

    // Buat JSON data dengan tipe field Firestore
    FirebaseJson json;
    json.set("fields/height/doubleValue", height);
    json.set("fields/pressure/doubleValue", pressure);
    json.set("fields/water_volume/doubleValue", water_volume);
    json.set("fields/timestamp/timestampValue", timestampStr);

    // Convert JSON ke string
    String jsonStr;
    json.toString(jsonStr, false);

    // Kirim data ke Firestore
    if (Firebase.Firestore.createDocument(&fbdo, FIREBASE_PROJECT_ID, "", "tb_ukuran", "", jsonStr.c_str(), "")) {
      Serial.println("Data berhasil dikirim ke Firestore!");
      Serial.println("Timestamp: " + timestampStr);
    } else {
      Serial.println("Gagal mengirim data ke Firestore: " + fbdo.errorReason());
    }

    
    if (Firebase.RTDB.setFloat(&fbdo, "tb_ukuran/pressure", pressure)){
      Serial.println("PASSED");
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
      Serial.print("VALUE: ");
      Serial.println(pressure);
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }

    if (Firebase.RTDB.setFloat(&fbdo, "tb_ukuran/height", height)){
      Serial.println("PASSED");
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
      Serial.print("VALUE: ");
      Serial.println(height);
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }

    if (Firebase.RTDB.setFloat(&fbdo, "tb_ukuran/water_volume", water_volume)){
      Serial.println("PASSED");
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
      Serial.print("VALUE: ");
      Serial.println(water_volume);
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }

    delay(180000);
  }
}
