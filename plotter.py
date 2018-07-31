import avro.schema
from avro.datafile import DataFileReader, DataFileWriter
from avro.io import DatumReader, DatumWriter
import datetime
import os.path
import boto
from boto.s3.bucket import Bucket
from boto.s3.key import Key
import traceback

import matplotlib.pyplot as plt
import matplotlib.animation as animation
from matplotlib import style


count = 0
bucket_name = "iot-data"
my_topic = "avro-demo"
flush_size = 3

offset = 0
prefix = "topics/"+my_topic+"/partition=0/"+my_topic+"+0+"

global temp
"""Takes an Avro file with the appropriate Schema and returns a triple of
three lists... (temperature, humidities, times)"""

'''
def fileParser(fileName):
  print(fileName)
  reader = DataFileReader(open(fileName, "rb"), DatumReader())
  humidities = []
  temps = []
  times = []
  for user in reader:
      humidities.append(user["Humidity"])
      temps.append(user["Temperature"])
      time = user["Timestamp"]
      date = datetime.datetime(time["Year"], time["Month"], time["Day"], time["Hour"], time["Second"])
      times.append(date.timestamp())
  reader.close()
  return (temps, humidities, times)
'''

"""Generates a string with the name of the file from 
topic, partition, and offset"""

def fileNameGenerator(topic, partition, offset):
  count = 1
  leftOver = offset
  while (leftOver // 10 != 0):
    leftOver = leftOver // 10
    count += 1
  stringOffset = ""
  for i in range(10 - count):
    stringOffset += "0"
  stringOffset += str(offset)
  fileName = topic + "+" + str(partition) + "+" + str(stringOffset) + ".avro"
  return fileName

def pull_from_hyperstore(key_name):
  try:
    conn = boto.connect_s3(host = 'tims4.mobi-cloud.com', port=80, is_secure = False) 
    print(conn, "connection made")
    bucket = Bucket(conn, bucket_name)
    #bucket = conn.get_bucket(bucket_name)
    #print(bucket)
    gkey = Key(bucket=bucket, name=key_name)
    #bucket.download_file(get_key_name(), get_key_name())
    data = gkey.get_contents_as_string()
    print("downloaded")
    return data
  except Exception as e:
    print(e)
    traceback.print_exc()

def fileParser(avro_data):
  reader = DataFileReader(avro_data, DatumReader())
  humidities = []
  temps = []
  times = []
  for user in reader:
      humidities.append(user["Humidity"])
      temps.append(user["Temperature"])
      time = user["Timestamp"]
      date = datetime.datetime(time["Year"], time["Month"], time["Day"], time["Hour"], time["Minute"], time["Second"])
      times.append(date.timestamp())
  reader.close()
  return (temps, humidities, times)


print("Trying new things")
(temps, humidities, times) = fileParser("avro-temp-data+0+0000000006.avro")
print(temps)
print(humidities)
print(times)

topic = "avro-temp-data"
partition = 0
offset = 3

print(fileNameGenerator(topic, partition, offset))

# Example file name listed below for reference
#/Users/philiplassen/Downloads/avro-temp-data+0+0000000006.avro

fig = plt.figure()
ax1 = fig.add_subplot(1,1,1)

"""def animate(i):
    offset = 0
    temps = []
    humidities = []
    times = []
    currentFile = fileNameGenerator(topic, partition, offset)
    print(os.path.isfile(currentFile))
    #while (os.path.isfile(currentFile)):
    while (offset < 9): 
      (temp, hum, time) = fileParser(currentFile)
      temps += temp
      humidities += hum
      time += time
      offset += 3
      currentFile = fileNameGenerator(topic, partition, offset)
      print(currentFile)
    ax1.plot(times, temps)
"""
def animate(i):
    print("Starting animate")
    global count
    count += 1
    print(count)
    offset = 0
    temps = []
    humidities = []
    times = []
    currentFile = fileNameGenerator(topic, partition, offset)
    while (os.path.isfile(currentFile)):
      (temp, hum, time) = fileParser(currentFile)
      temps += temp
      humidities += hum
      times += time
      offset += 3
      currentFile = fileNameGenerator(topic, partition, offset)
      print(currentFile)
    ax1.plot(times, temps)


ani = animation.FuncAnimation(fig, animate, interval=3000)

plt.show()
