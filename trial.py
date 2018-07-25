import RPi.GPIO as GPIO
import dht11
import time
import datetime

# initialize GPIO
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.cleanup()

# read data using pin 14


instance = dht11.DHT11(pin=17)

result = instance.read()
if result.is_valid():
    print(str(datetime.datetime.now()) + " " + str(result.temperature) + " " + str(result.humidity))
