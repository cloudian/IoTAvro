'''
import avro.schema
from avro.datafile import DataFileReader, DataFileWriter
from avro.io import DatumReader, DatumWriter
import datetime
'''

import os
import os.path
import boto
from boto.s3.bucket import Bucket
from boto.s3.key import Key
import traceback

import matplotlib.pyplot as plt
import matplotlib.animation as animation 

import sys
import json

if sys.argv==[''] or len(sys.argv)<2:
  ProducerID = "Computer"

else:
  ProducerID = sys.argv[1]

my_topic = "scaled-topic"
bucket_name = "iot-data"
count = 0
flush_size = 1

offset = 0
partition = 0
prefix = "topics/"+str(my_topic)+"/ProducerID="+str(ProducerID)+"/"+str(my_topic)+"+"+str(partition)+"+"

temps = []
humidities = []
times = []

"""Takes an Avro file with appropriate Schema and returns a triple of
three lists... (temperature, humidities, times)"""


def fileParser(fileName):

  json_file = open('this.json')
  json_str = json_file.read()
  json_data = json.loads(json_str)
  
  t_temps = [json_data["Humidity"]]
  t_humidities = [json_data["Temperature"]]
  t_times = [json_data["Timestamp"]["Hour"]*3600 + json_data["Timestamp"]["Minute"]*60 + json_data["Timestamp"]["Second"]]

  #reader = DataFileReader(open(fileName, "rb"), DatumReader())
  '''
  for user in reader:
      t_humidities.append(user["Humidity"])
      t_temps.append(user["Temperature"])
      time = user["Timestamp"]
      #date = datetime.datetime(time["Year"], time["Month"], time["Day"], time["Hour"], time["Minute"], time["Second"], 0)
      phil_time = 60*60*time["Hour"] + 60*time["Minute"] + time["Second"]
      t_times.append(phil_time)
      '''
  #print(str(humidities) + "\n" + str(temps) + "\n")
  #reader.close()
  return (t_temps, t_humidities, t_times)


"""Generates a string with the name of the file from 
topic, partition, and offset"""


def fileNameGenerator(offset):
  suffix = str(offset).zfill(10) + ".json"
  my_key = prefix + suffix
  return my_key

def pull_from_hyperstore(key_name):
    conn = boto.connect_s3(host = 'tims4.mobi-cloud.com', port=80, is_secure = False) 
    bucket = Bucket(conn, bucket_name)
    gkey = Key(bucket=bucket, name=key_name)
    gkey.get_contents_to_filename("this.json")

# Example file name listed below for reference
#/Users/philiplassen/Downloads/avro-temp-data+0+0000000006.avro

def animate(i):
  #print("Starting animate")
  global offset
  try:
    f = open("this.json", "w+")
    f.close()
    attempts = 0
    prev_offset=offset
    #key = "topics/timdemo/ProducerID=Computer/timdemo+0+0000000003.avro"
    #pull_from_hyperstore(key)
    
    while (True):
      key = fileNameGenerator(offset)
      try:
        pull_from_hyperstore(key)
        break
      except Exception as e:
        if attempts==5:
          offset = prev_offset
          attempts = 0
          break
        else:
          offset += 1
          attempts += 1
        pass
        

    global temps, humidities, times
    temp, humidity, time = fileParser("this.json")
    offset = offset + flush_size
    os.remove("this.json")
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
fig.suptitle(ProducerID, fontsize=14, fontweight='bold')
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



