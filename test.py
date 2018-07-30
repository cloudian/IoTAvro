#Kodiak Conrad
#Cloudian, Inc
#Avro Grapher

import boto
import traceback


bucket_name = "iot-data"
my_topic = "avro-demo"
flush_size = 3


try:
	conn = boto.connect_s3(host = 'http://tims4.mobi-cloud.com:80', 
		aws_access_key_id = "00c36f16c2600f70ae60",
		aws_secret_access_key = "XsSbmCIfcYrX5NdCBj7n1QSaU2lhdgDJJBDlT7VE",
		is_secure = False)                                                                                                                                                       
	print conn
	print("connection made")
	bucket = conn.get_bucket(bucket_name)
	print(bucket)
	bucket.download_file(get_key_name(), get_key_name())
	print("downloaded")
except Exception as e:
	print(e)
	traceback.print_exc()
#gkey = Key(bucket=bucket, name=get_key_name())

offset = 0
prefix = "topics/"+my_topic+"/partition=0/"+my_topic+"+0+"

def get_key_name():
	'''
	suffix = ("0" * (9 - (offset / 10))) + str(offset)
	my_key = prefix+suffix
	offset = offset + flush_size
	return my_key'''
	return "topics/avro-demo/partition=0/avro-demo+0+0000000000.avro"