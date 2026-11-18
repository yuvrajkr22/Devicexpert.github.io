#include <WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
const char* ssid = "YOUR_WIFI_SSID";
const char* password = "YOUR_WIFI_PASSWORD";
String tel_t = "devicexpert/v1/user_demo/esp32_sensor_node_01/telemetry";
String stat_t = "devicexpert/v1/user_demo/esp32_sensor_node_01/status";
WiFiClient espClient; PubSubClient client(espClient);
void setup() { Serial.begin(115200); WiFi.begin(ssid, password); while(WiFi.status()!=WL_CONNECTED) delay(500); client.setServer("broker.hivemq.com", 1883); }
void loop() {
  while (!client.connected()) { if (client.connect("esp32_sensor_node_01", NULL, NULL, stat_t.c_str(), 1, true, "OFFLINE")) client.publish(stat_t.c_str(), "ONLINE", true); else delay(5000); }
  client.loop();
  StaticJsonDocument<256> doc; doc["type"] = "IOT"; doc["temperatureC"] = 24.5; doc["humidityPct"] = 60.2; doc["voltageV"] = 3.3; doc["currentMa"] = 120.0; doc["powerMw"] = 396.0; doc["rssiDbm"] = WiFi.RSSI(); doc["uptimeSeconds"] = millis()/1000;
  char buffer[256]; serializeJson(doc, buffer);
  client.publish(tel_t.c_str(), buffer);
  delay(2000);
}