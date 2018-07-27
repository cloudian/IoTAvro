import sys
import dht11
import time
import datetime
import RPi.GPIO as GPIO
# initialize GPIO

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.cleanup()

# read data using pin 17


instance = dht11.DHT11(pin=17)

if len(sys.argv) == 1:
  result = instance.read()
  if result.is_valid():
    print(str(datetime.datetime.now()) + " " + str(result.temperature) + " " + str(result.humidity))
else:
  humiditiy = 70
  temp = 20
  print(str(datetime.datetime.now()) + " " + str(temp) + " " + str(humidity))
