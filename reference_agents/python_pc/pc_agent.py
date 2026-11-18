import time, json, psutil
import paho.mqtt.client as mqtt
USER_ID, DEVICE_ID, BROKER, PORT = "user_demo", "desktop_workstation_01", "broker.hivemq.com", 1883
TELEMETRY_TOPIC = f"devicexpert/v1/{USER_ID}/{DEVICE_ID}/telemetry"
STATUS_TOPIC = f"devicexpert/v1/{USER_ID}/{DEVICE_ID}/status"

def on_connect(c, u, f, rc): c.publish(STATUS_TOPIC, "ONLINE", retain=True)
client = mqtt.Client(client_id=DEVICE_ID)
client.will_set(STATUS_TOPIC, "OFFLINE", retain=True)
client.on_connect = on_connect
client.connect(BROKER, PORT, 60)
client.loop_start()
boot_time = psutil.boot_time()

try:
    while True:
        c, m, d, n = psutil.cpu_percent(interval=1), psutil.virtual_memory(), psutil.disk_usage('/'), psutil.net_io_counters()
        t = psutil.sensors_temperatures().get("coretemp", [type('obj', (object,), {'current': None})])[0].current if hasattr(psutil, "sensors_temperatures") else None
        client.publish(TELEMETRY_TOPIC, json.dumps({"type": "COMPUTER", "cpuUsagePct": c/100.0, "ramUsedBytes": m.used, "diskUsedBytes": d.used, "netRxBps": n.bytes_recv, "netTxBps": n.bytes_sent, "cpuTempC": t, "uptimeSeconds": int(time.time()-boot_time), "topProcessCount": len(psutil.pids())}))
        time.sleep(2)
except KeyboardInterrupt:
    client.publish(STATUS_TOPIC, "OFFLINE", retain=True)
    client.disconnect()
    client.loop_stop()