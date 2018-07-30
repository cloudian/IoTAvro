#Kodiak Conrad
#Cloudian, Inc
#Avro Grapher

#import boto3
from boto.s3.connection import S3Connection
import StringIO
from avro.datafile import DataFileReader, DataFileWriter
from avro.io import DatumReader, DatumWriter

conn = boto.connect_s3()
bucket_name = "iot-data"
my_topic = "avro-demo"
flush_size = 3

try:
	bucket = conn.get_bucket(bucket_name)
	bucket.download_file(get_key_name(), get_key_name())
except Exception as e:
	print(e)
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