
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
import re
import matplotlib.pyplot as plt
import matplotlib.animation as animation 

import sys
import json

pc = 1

my_topic = "scaled-topic"

if sys.argv==[''] or len(sys.argv)<2:
  pc = 1

else:
  my_topic = sys.argv[1]
  pc = int(sys.argv[2])
  print(pc)
bucket_name = "iot-data"
count = 0
flush_size = 1

offset = 0
partition = 0

def pids(number):
  prefix = "topics/"+str(my_topic)+"/ProducerID="+str(number)+"/"+str(my_topic)+"+"

def keyToFileName(key):
  return str(key)[15:-1]

idToLastResult = dict()

for i in range(pc):
  idToLastResult[i] = ""
  
def populate(dic):
  for i in range(pc):
    dic[i] = []

temps = dict()
hums = dict()
time = dict()

populate(temps)
populate(hums)
populate(time)
  
print(temps)
print(hums)
print(time)
  







'''
r2 = bucket.get_all_keys(max_keys = 10,  headers=None, prefix= "topics/"+str(my_topic)+"/ProducerID=", marker = keyToFileName(results[9]))
r3 = bucket.get_all_keys(max_keys = 10, headers=None, prefix="topics/"+str(my_topic)+"/ProducerID=", marker = keyToFileName(r2[9]))
print(results)
print()
print(len(r2))
print()
print()
print(r3)
print()
print()
print()
results[9].get_contents_to_filename("this.json")
print(results[9] == results[9])
print(str(r2[9]) == str(results[9]))
print(r2[9])
print(results[9])
print(keyToFileName(results[9]))
'''


def fileParser(fileName):
  json_file = open(fileName)
  print("opened")
  json_str = json_file.read()
  print("read")
  json_data = json.loads(json_str)
  print("read json")

  t_temps = [json_data["Humidity"]]
  t_humidities = [json_data["Temperature"]]
  t_times = [json_data["Timestamp"]["Hour"]*3600 + json_data["Timestamp"]["Minute"]*60 + json_data["Timestamp"]["Second"]]

  return (t_temps, t_humidities, t_times)



'''
gkey = Key(bucket=bucket, name=key_name)
gkey.get_contents_to_filename("this.json")
'''

'''
Takes an Avro file with the appropriate Schema and returns a triple of
three lists... (temperature, humidities, times)
 fileParser(fileName):

  json_file = open('this.json')
  print("opened")
  json_str = json_file.read()
  print("read")
  json_data = json.loads(json_str)
  print("read json")
  
  t_temps = [json_data["Humidity"]]
  t_humidities = [json_data["Temperature"]]
  t_times = [json_data["Timestamp"]["Hour"]*3600 + json_data["Timestamp"]["Minute"]*60 + json_data["Timestamp"]["Second"]]

  #reader = DataFileReader(open(fileName, "rb"), DatumReader())
  for user in reader:
      t_humidities.append(user["Humidity"])
      t_temps.append(user["Temperature"])
      time = user["Timestamp"]
      #date = datetime.datetime(time["Year"], time["Month"], time["Day"], time["Hour"], time["Minute"], time["Second"], 0)
      phil_time = 60*60*time["Hour"] + 60*time["Minute"] + time["Second"]
      t_times.append(phil_time)
  #print(str(humidities) + "\n" + str(temps) + "\n")
  #reader.close()
  return (t_temps, t_humidities, t_times)
'''

'''Generates a string with the name of the file from 
topic, partition, and offset
'''

def fileNameGenerator(offset):
  suffix = str(offset).zfill(10) + ".json"
  my_key = prefix + suffix
  return my_key




def getNextResults(pid):
  conn = boto.connect_s3(aws_access_key_id = "00c36f16c2600f70ae60", aws_secret_access_key = "XsSbmCIfcYrX5NdCBj7n1QSaU2lhdgDJJBDlT7VE", host = 'tims4.mobi-cloud.com', port=80, is_secure = False)
  bucket = Bucket(conn, bucket_name)
  results = bucket.get_all_keys(max_keys=1, headers=None, prefix= "topics/"+str(my_topic)+"/ProducerID=" + str(pid) + "/", marker= idToLastResult[pid])
  if len(results) == 1:
    print(keyToFileName(results[0]))
  if len(results) > 0:
    idToLastResult[pid] = keyToFileName(results[0])
    results[0].get_contents_to_filename("this.json")
    return results[0]
  else:
    return None

def pull_from_hyperstore(key_name):
    conn = boto.connect_s3(aws_access_key_id = "00c36f16c2600f70ae60", aws_secret_access_key = "XsSbmCIfcYrX5NdCBj7n1QSaU2lhdgDJJBDlT7VE",host = 'tims4.mobi-cloud.com', port=80, is_secure = False) 
    bucket = Bucket(conn, bucket_name)
    gkey = Key(bucket=bucket, name=key_name)
    gkey.get_contents_to_filename("this.json")

# Example file name listed below for reference
#/Users/philiplassen/Downloads/avro-temp-data+0+0000000006.avro

def animate(i):
  try:
    f = open("this.json", "w+")
    f.close()
    for i in range(pc):
      if None != getNextResults(i):
        global temps, hums, time
        te, h, t = fileParser("this.json")
        print("parsed file")
        print(i)
        temps[i] += te
        hums[i] += h
        time[i] += t    
        os.remove("this.json")
      ax1.plot(time[i], temps[i])
      ax2.plot(time[i], hums[i])
    print(time)
    print(len(time[0]))
    print(len(time[1]))
    print(len(time[2]))
  except Exception as e:
    print(e)
    traceback.print_exc()
    ax1.plot(time, temps)
    ax2.plot(time, humidities)

#formatting guide link https://matplotlib.org/users/text_intro.html
fig = plt.figure()
fig.suptitle("DATA", fontsize=14, fontweight='bold')
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
