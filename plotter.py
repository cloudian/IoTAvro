import avro.schema
from avro.datafile import DataFileReader, DataFileWriter
from avro.io import DatumReader, DatumWriter
import datetime
import os
import os.path
import boto
from boto.s3.bucket import Bucket
from boto.s3.key import Key
import traceback

import matplotlib.pyplot as plt
import matplotlib.animation as animation 

import sys

if sys.argv==[''] or len(sys.argv)<2:
  my_topic = "p-1"
else:
  my_topic = sys.argv[1]


bucket_name = "iot-data"
count = 0
flush_size = 3
ProducerID = "Computer"

offset = 0
partition = 0
prefix = "topics/"+str(my_topic)+"/ProducerID="+str(ProducerID)+"/"+str(my_topic)+"+"+str(partition)+"+"

temps = []
humidities = []
times = []

"""Takes an Avro file with the appropriate Schema and returns a triple of
three lists... (temperature, humidities, times)"""


def fileParser(fileName):
  reader = DataFileReader(open(fileName, "rb"), DatumReader())
  t_temps = []
  t_humidities = []
  t_times = []
  global offset, zero_time
  for user in reader:
      t_humidities.append(user["Humidity"])
      t_temps.append(user["Temperature"])
      time = user["Timestamp"]
      #date = datetime.datetime(time["Year"], time["Month"], time["Day"], time["Hour"], time["Minute"], time["Second"], 0)
      phil_time = 60*60*time["Hour"] + 60*time["Minute"] + time["Second"]
      t_times.append(phil_time)
  #print(str(humidities) + "\n" + str(temps) + "\n")
  reader.close()
  return (t_temps, t_humidities, t_times)


"""Generates a string with the name of the file from 
topic, partition, and offset"""


def fileNameGenerator(offset):
  suffix = str(offset).zfill(10) + ".avro"
  my_key = prefix + suffix
  return my_key

def pull_from_hyperstore(key_name):
  try:
    conn = boto.connect_s3(host = 'tims4.mobi-cloud.com', port=80, is_secure = False) 
    bucket = Bucket(conn, bucket_name)
    gkey = Key(bucket=bucket, name=key_name)
    gkey.get_contents_to_filename("this.avro")
  except:
    a = ""

    #print(e)

# Example file name listed below for reference
#/Users/philiplassen/Downloads/avro-temp-data+0+0000000006.avro

def animate(i):
  #print("Starting animate")
  global offset
  key = fileNameGenerator(offset)
  try:
    f = open("this.avro", "w+")
    f.close()
    pull_from_hyperstore(key)
    global temps, humidities, times
    temp, humidity, time = fileParser("this.avro")
    offset = offset + flush_size
    os.remove("this.avro")
    temps += temp
    humidities += humidity
    times += time
    print(temps)
    print(humidities)
    print(str(times))
    # need to clear file
    ax1.plot(times, temps)
    ax2.plot(times, humidities)

  except Exception as e:
    #print(e)
    #traceback.print_exc()
    ax1.plot(times, temps)
    ax2.plot(times, humidities)

#formatting guide link https://matplotlib.org/users/text_intro.html
fig = plt.figure()
fig.suptitle(my_topic, fontsize=14, fontweight='bold')
ax1 = fig.add_subplot(2,1,1)
ax1.set_xlabel('Seconds')
ax1.set_ylabel('Celsius')
ax1.set_title("Temperature")
ax2 = fig.add_subplot(2,1,2)
ax2.set_title("Humidity")
ax2.set_xlabel('Seconds')
ax2.set_ylabel('Percentage %')
#pull_from_hyperstore(key)
#fileParser("this.avro")
ani = animation.FuncAnimation(fig, animate, interval=1000)

plt.show()



