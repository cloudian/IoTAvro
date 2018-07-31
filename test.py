#Kodiak Conrad
#Cloudian, Inc
#Avro Grapher

import boto
from boto.s3.bucket import Bucket
from boto.s3.key import Key
import traceback


bucket_name = "iot-data"
my_topic = "avro-demo"
flush_size = 3

def get_key_name():
	'''
	suffix = ("0" * (9 - (offset / 10))) + str(offset)
	my_key = prefix+suffix
	offset = offset + flush_size
	return my_key'''
	return "topics/avro-demo/partition=0/avro-demo+0+0000000000.avro"

try:
	conn = boto.connect_s3(host = 'tims4.mobi-cloud.com', port=80, is_secure = False)                                                                                                                                                       
	print conn
	print("connection made")
	bucket = Bucket(conn, bucket_name)
	#bucket = conn.get_bucket(bucket_name)
	#print(bucket)
	gkey = Key(bucket=bucket, name=get_key_name())
	#bucket.download_file(get_key_name(), get_key_name())
	print(gkey.get_contents_as_string())
	print("downloaded")
except Exception as e:
	print(e)
	traceback.print_exc()


#offset = 0
#prefix = "topics/"+my_topic+"/partition=0/"+my_topic+"+0+"

