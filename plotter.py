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

count = 0
bucket_name = "iot-data"
my_topic = "avro-demo"
flush_size = 3

offset = 0
partition = 0
prefix = "topics/"+str(my_topic)+"/partition="+str(partition)+"/"+str(my_topic)+"+"+str(partition)+"+"

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
    print(conn, "connection made")
    bucket = Bucket(conn, bucket_name)
    #bucket = conn.get_bucket(bucket_name)
    print(bucket)
    gkey = Key(bucket=bucket, name=key_name)
    #bucket.download_file(get_key_name(), get_key_name())
    try:
      gkey.get_contents_to_filename("this.avro")
    except:
      print("ya fucked up")
    print("downloaded")
  except Exception as e:
    print(e)


#print(fileNameGenerator(topic, partition, offset))

# Example file name listed below for reference
#/Users/philiplassen/Downloads/avro-temp-data+0+0000000006.avro

def animate(i):
  #print("Starting animate")
  global offset
  key = fileNameGenerator(offset)
  #key = "topics/avro-demo/partition=0/avro-demo+0+0000000000.avro"
  offset = offset + flush_size
  try:
    f = open("this.avro", "w+")
    f.close()
    pull_from_hyperstore(key)
    global temps, humidities, times
    temp, humidity, time = fileParser("this.avro")
    os.remove("this.avro")
    temps += temp
    humidities += humidity
    times += time
    print(temps)
    print(humidities)
    print("\n"+str(times)+"\n")
    # need to clear file
    ax1.plot(times, temps)
    ax2.plot(times, humidities)

  except Exception as e:
    print(e)
    traceback.print_exc()
    print("shit, we should put something in here")


fig = plt.figure()
ax1 = fig.add_subplot(2,1,1)
ax2 = fig.add_subplot(2,1,2)

#pull_from_hyperstore(key)
#fileParser("this.avro")
ani = animation.FuncAnimation(fig, animate, interval=5000)

plt.show()



