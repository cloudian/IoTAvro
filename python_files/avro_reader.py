"""Takes an avro file name as a command line arguement 
and prints data in the avro file into std out"""
import avro.schema
from avro.datafile import DataFileReader, DataFileWriter
from avro.io import DatumReader, DatumWriter
import sys

reader = DataFileReader(open(sys.argv[1], "rb"), DatumReader())
for user in reader:
    print(user)
    print(type(user))
reader.close()
