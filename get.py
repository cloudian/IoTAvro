#!/usr/bin/env python
# -*- coding: utf-8 -*-

try:
    from ast import literal_eval
except ImportError:
    def literal_eval(arg):
        return eval(arg)

import re
import os
import sys
import tempfile
import hashlib
import urlparse
import httplib
import hashlib
import base64
from session import Session, bytes_to_string
try:
    from hashlib import md5
except ImportError:
    from md5 import md5

import boto.utils
from boto.s3.key import Key
from boto.s3.deletemarker import DeleteMarker
from boto.s3.bucket import Bucket
from boto.exception import S3ResponseError

USAGE = """Get an object:

Usage: get.py [-v X][-f X][-r X][-o X][-H x][-Q X] [-n] [-t] key  ... get object

 -f file     ... write contents to file or stdout
 -F file     ... file contents will be used as object name
 -r range    ... get a byte range "0-9"
 -o dict     ... override response dictionary using keys:
                   "ct" : Content-Type
                   "cl" : Content-Language
                   "e"  : Expires
                   "cc" : Cache-Control
                   "cd" : Content-Disposition
                   "ce" : Content-Encoding
                 Ex: -o '{"ct":"text/plain"}'
 -n          ... GET Object tagging
 -t          ... GET Object torrent
 -H dict     ... Add headers from dictorary to request
                 Ex: -H '{"If-Modified-Since":"Tue, 30 Aug 2011 05:26:48 GMT"}'
 -Q secs     ... Query String Authorization with expires in secs
 --storage=X ... Display/Don't Display the storage class (True/False)
 --ssec=pw   ... Use Customer Key SSE with the given password
 --head      ... HEAD Object only
 --get       ... GET Object only
"""

sess = Session(sys.argv[1:], "v:f:r:o:F:H:Q:nt", USAGE, longopts=['storage=', 'ssec=', 'head', 'get'])
print "Using '%s' bucket '%s' and user '%s'\n" % (sess.service, sess.bucket, str(sess.get_user()))
conn = sess.get_con()
bucket = Bucket(conn, sess.bucket)

def get_reshdrs(stringdict):
    out = {}
    inn = literal_eval(stringdict)
    if inn.has_key("ct"):
        out["response-content-type"] = inn["ct"]
    if inn.has_key("cl"):
        out["response-content-language"] = inn["cl"]
    if inn.has_key("e"):
        out["response-expires"] = inn["e"]
    if inn.has_key("cc"):
        out["response-cache-control"] = inn["cc"]
    if inn.has_key("cd"):
        out["response-content-disposition"] = inn["cd"]
    if inn.has_key("ce"):
        out["response-content-encoding"] = inn["ce"]
    return out

def print_header(name, hval, gval, head_only=False, get_only=False, torrent=False):
    if head_only:
        if hval:
            print "%s:" % name, hval
    elif get_only:
        if gval:
            print "%s:" % name, gval
    else:
        if hval or gval:
            if torrent:
                if hval is not None:
                    print "H %s:" % name, hval
                if gval is not None:
                    print "G %s:" % name, gval
            else:
                if hval == gval:
                    print "%s:" % name, hval
                else:
                    print "BAD %s:" % name, hval, "!=",  gval

def print_metadata(hkey, gkey, head_only=False, get_only=False):
    if head_only:
        if hkey is not None and hkey.metadata:
            hmks = set(hkey.metadata.keys())
            print "Common Metadata:"
            for k in hmks:
                hv = hkey.metadata[k]
                print "", k, "=>", hv
    elif get_only:
        if gkey is not None and gkey.metadata:
            gmks = set(gkey.metadata.keys())
            print "Common Metadata:"
            for k in gmks:
                gv = gkey.metadata[k]
                print "", k, "=>", gv
    else:
        if hkey is not None and hkey.metadata and gkey is not None and gkey.metadata:
            hmks = set(hkey.metadata.keys())
            gmks = set(gkey.metadata.keys())
            if hmks - gmks:
                print "HEAD Metadata not in GET: ", (hmks - gmks)
            if gmks - hmks:
                print "GET Metadata not in HEAD: ", (gmks - hmks)
            common_keys = gmks.intersection(hmks)
            print "Common Metadata:"
            for k in common_keys:
                hv = hkey.metadata[k]
                gv = gkey.metadata[k]
                if hv == gv:
                    print "", k, "=>", hv
                else:
                    print "", k, "=>", hv, "!=", gv

def read_call_back(sofar, total):
    bstring = bytes_to_string(sofar)
    if total > 0:
        print "\rread: %s - %d%%  " % (bstring, int(sofar*100/total)),
    else:
        print "\rread: %s  " % bstring,
    sys.stdout.flush()

def get_query_auth_url(method, key, version_id, expires, headers, response_headers):
    hdrs = headers.copy()
    for h in ('x-amz-server-side-encryption-customer-key', 'x-amz-server-side-encryption-customer-key-MD5'):
        if hdrs.has_key(h):
            del hdrs[h]
    return key.generate_url(expires, method=method, headers=hdrs, response_headers=response_headers, version_id=version_id)
    
def do_query_auth(method, key, url, headers, fp=None):
    """performs the request and updates the key."""
    provider = key.bucket.connection.provider
    up = urlparse.urlsplit(url)
    if up.scheme == 'https':
        secure = True
    else:
        secure = False
    con = conn.get_http_connection(up.hostname, up.port, secure)
    con.request(method, up.path + '?' + up.query, headers=headers)
    resp = con.getresponse()
    if resp.status == 307:
        next_url = resp.getheader("location", None)
        if next_url is not None:
            body = resp.read()
            return do_query_auth(method, key, next_url, headers, fp)
    if resp.status < 199 or resp.status > 299:
        body = resp.read()
        raise provider.storage_response_error(resp.status, resp.reason, body)
    response_headers = resp.msg
    for name,value in response_headers.items():
        if (name.lower() == 'content-length' and 'Content-Range' not in response_headers):
            key.size = int(value)
        elif name.lower() == 'content-range':
            end_range = re.sub('.*/(.*)', '\\1', value)
            key.size = int(end_range)
        elif name.lower() == 'etag':
            key.etag = value
        elif name.lower() == 'content-type':
            key.content_type = value
        elif name.lower() == 'content-encoding':
            key.content_encoding = value
        elif name.lower() == 'last-modified':
            key.last_modified = value
        elif name.lower() == 'cache-control':
            key.cache_control = value
        elif name.lower() == 'content-disposition':
            key.content_disposition = value
    key.metadata = boto.utils.get_aws_metadata(response_headers, provider)
    key.handle_version_headers(resp)
    key.handle_encryption_headers(resp)
    if method == "GET":
        m = md5()
        while True:
            bytes = resp.read(key.BufferSize)
            if not bytes:
                break
            m.update(bytes)
            fp.write(bytes)
        key.md5 = m.hexdigest()
    return url # key is updated and we just return the url

file=None
object_filename=None
ver=None
headers={}
res_hdrs={}
query_exp=None
display_sc=False
ssec=None
head_only=False
get_only=False
tagging=None
torrent=False

for opt,arg in sess.opts:
    if opt == "-v":
        ver = arg
    elif opt == "-f":
        file = arg
    elif opt == "-F":
        object_filename = arg
    elif opt == "-r":
        headers["Range"] = "bytes=%s" % arg
    elif opt == "-o":
        res_hdrs = get_reshdrs(arg)
    elif opt == "-n":
        tagging = True
    elif opt == "-t":
        torrent = True
    elif opt == "-H":
        hdrs = literal_eval(arg)
        headers.update(hdrs)
    elif opt == "-Q":
        query_exp = int(arg)
    elif opt == "--storage":
        display_sc = arg in ['true', 'True', 't', '1']
    elif opt == "--ssec":
        ssec = arg
    elif opt == "--head":
        head_only = True
    elif opt == "--get":
        get_only = True

if object_filename is not None:
    with open(object_filename, 'r') as f:
        for row in f:
            key_name = row.strip().decode('utf-8')
else:
    if len(sess.args) != 1:
        sess.usage_exit("Error: missing key\n")
    key_name = sess.args[0].decode('utf-8')

if file and not file == "-":
    fp = open(file, 'w+b')
else:
    fp = tempfile.TemporaryFile()

if ssec is not None:
    # Setup the headers for ssec.
    # use md5 of ssec pass for key as its 32 bytes
    ssec_md5 = hashlib.md5()
    ssec_md5.update(ssec)
    ssec_key = ssec_md5.hexdigest()
    headers = {} if headers is None else headers
    headers['x-amz-server-side-encryption-customer-algorithm'] = 'AES256'
    headers['x-amz-server-side-encryption-customer-key'] = base64.b64encode(ssec_key)
    ssec_md5 = hashlib.md5()
    ssec_md5.update(ssec_key)
    headers['x-amz-server-side-encryption-customer-key-MD5'] = base64.b64encode(ssec_md5.digest())

if query_exp is not None:
    hkey = Key(bucket=bucket, name=key_name)
    gkey = Key(bucket=bucket, name=key_name)
    hurl = get_query_auth_url("HEAD", hkey, ver, query_exp, headers, res_hdrs)
    gurl = get_query_auth_url("GET", gkey, ver, query_exp, headers, res_hdrs)
    print "Using generated Query String:"
    print "=>", gurl
    if headers:
        # x-amz* type headers are included in the url so we need to
        # test the HEAD/GET without using them as headers.
        hdr_prefix = conn.provider.header_prefix
        for k, v in headers.items():
            if k.startswith(hdr_prefix) and 'server-side-encryption-customer' not in k:
                del headers[k]
        if headers:
            print "\nNOTE: you will need to send the provided headers also."
            print headers
    print ""
    eh = None
    if not get_only:
        try:
            do_query_auth("HEAD", hkey, hurl, headers)
        except S3ResponseError as eh:
            pass
    if not head_only:
        try:
            do_query_auth("GET", gkey, gurl, headers, fp)
            if eh is not None:
                print "=====> HEAD", eh.status, "but GET OK <======\n"
                print "%s" % eh
                sys.exit(1)
        except S3ResponseError as eg:
            if eh is None:
                if get_only:
                    print "=====> GET", eg.status, "threw exception <=====\n"
                else:
                    print "=====> HEAD OK, but GET", eg.status, "threw exception <=====\n"
            else:
                if get_only:
                    print "=====> GET", eg.status, "threw exception <=====\n"
                else:
                    print "=====> HEAD", eh.status, "and GET", eg.status, "threw exception <=====\n"
            print "%s" % eg
            sys.exit(1)
    else:
        if eh:
            print "\n=====> HEAD", eh.status, "failed <======\n"
            sys.exit(1)
elif tagging is not None:
    kname = key_name
    print "Getting Object Tagging for: ", kname, ver
    tkey = Key(bucket=bucket, name=kname, version_id=ver)
    tagsConfig = tkey.get_tags();
    print tagsConfig.to_xml()
    sess.dump_gmt_usage()
    sys.exit(0)
else:
    # use head to get some values for the key. call it hkey
    # We'll silently compare stuff for when we do a get below.
    hkey = None
    eh = None
    cb = None
    if not get_only:
        try:
            hkey = bucket.get_key(key_name=key_name, headers=headers, version_id=ver, response_headers=res_hdrs)
        except S3ResponseError as eh:
            pass  # forbidden does this but not found doesn't.
    gkey = Key(bucket=bucket, name=key_name)
    if not head_only:
        try:
            num_cb = 100
            if hkey and hkey.size > 1048576:
                cb = read_call_back
            gkey.get_contents_to_file(fp, torrent=torrent, version_id=ver, headers=headers, response_headers=res_hdrs, cb=cb, num_cb=num_cb)
        except S3ResponseError as eg:
            if hkey:
                print "\n=====> HEAD ok, but GET threw exception <=====\n"
                print "H %s:" % hkey.name, hkey.last_modified, hkey.version_id, hkey.size, hkey.etag
                print_header("Content-Type", hkey.content_type, None, head_only=head_only, get_only=get_only)
                print_header("Content-Encoding", hkey.content_encoding, None, head_only=head_only, get_only=get_only)
                print_header("Cache-Control", hkey.cache_control, None, head_only=head_only, get_only=get_only)
                print_header("x-gmt-hyperstore", hkey.hyperstore, None, head_only=head_only, get_only=get_only)
                print_header("Restore Ongoing", hkey.ongoing_restore, None, head_only=head_only, get_only=get_only)
                print_header("Restore Expiry Date", hkey.expiry_date, None, head_only=head_only, get_only=get_only)
                print_header("ServerSideEncryption", hkey.encrypted, None, head_only=head_only, get_only=get_only)
                print_header("Replication Status", hkey.replication, None, head_only=head_only, get_only=get_only)
                print_header("Tagging Count", hkey.tagging_count, None, head_only=head_only, get_only=get_only)
                print_metadata(hkey, None, head_only=head_only, get_only=get_only)
                print "\n"
            elif eh:
                if get_only:
                    print "\n=====> GET", eg.status, "failed <======\n"
                else:
                    print "\n=====> HEAD", eh.status, "and GET", eg.status, "both failed <======\n"
            elif not eh:
                if get_only:
                    print "\n=====> GET", eg.status, "failed <======\n"
                else:
                    print "\n=====> HEAD 404 and GET", eg.status, "both failed <======\n"
            print "%s" % eg
            sess.dump_gmt_usage()
            sys.exit(1)
    else:
        # 403 or 404 with head only
        if eh:
            print "\n=====> HEAD", eh.status, "failed <======\n"
            sess.dump_gmt_usage()
            sys.exit(1)
        elif hkey is None:
            print "\n=====> HEAD 404 failed <======\n"
            sess.dump_gmt_usage()
            sys.exit(1)
    if not hkey:
        # GET ok, but HEAD is not. init new key and display anyway
        hkey = Key(bucket=bucket, name=key_name)
    if cb:
        print "\r",

# Just call the storage_class once as it does a get bucket
if display_sc:
    try:
        storage_class = gkey.storage_class
    except S3ResponseError as e:
        if e.status == 403:
            storage_class = '*'
        else:
            storage_class = '???'
else:
    storage_class = ''

if not get_only:
    print "H %s:" % hkey.name, hkey.last_modified, hkey.version_id, hkey.size, hkey.etag, storage_class
if not head_only:
    print "G %s:" % gkey.name, gkey.last_modified, gkey.version_id, gkey.size, gkey.etag, storage_class

print_header("Content-Type", hkey.content_type, gkey.content_type, head_only=head_only, get_only=get_only, torrent=torrent)
print_header("Content-Encoding", hkey.content_encoding, gkey.content_encoding, head_only=head_only, get_only=get_only, torrent=torrent)
print_header("Cache-Control", hkey.cache_control, gkey.cache_control, head_only=head_only, get_only=get_only, torrent=torrent)
print_header("x-gmt-hyperstore", hkey.hyperstore, gkey.hyperstore, head_only=head_only, get_only=get_only, torrent=torrent)
print_header("Restore Ongoing", hkey.ongoing_restore, gkey.ongoing_restore, head_only=head_only, get_only=get_only, torrent=torrent)
print_header("Restore Expiry Date", hkey.expiry_date, gkey.expiry_date, head_only=head_only, get_only=get_only, torrent=torrent)
print_header("ServerSideEncryption", hkey.encrypted, gkey.encrypted, head_only=head_only, get_only=get_only, torrent=torrent)
print_header("Replication Status", hkey.replication, gkey.replication, head_only=head_only, get_only=get_only, torrent=torrent)
print_header("Tagging Count", hkey.tagging_count, gkey.tagging_count, head_only=head_only, get_only=get_only, torrent=torrent)
print_metadata(hkey, gkey, head_only=head_only, get_only=get_only)

# TODO: use this info
seed,threads,parts = sess.get_cookie(gkey)

if not head_only and torrent is False:
    md5 = '"%s"' % gkey.md5
    if len(gkey.etag) == 34 and not headers.has_key("Range") and gkey.encrypted != "aws:kms":
        if md5 != gkey.etag:
            print "==> MD5 Mismatch:", md5
    else:
        # Can't check the MD5 - so just output it.
        print "Content MD5:", md5

if not head_only:
    fp.seek(0, os.SEEK_END)
    clen = fp.tell()
    if not torrent and not headers.has_key("Range") and clen != gkey.size:
        print "==> Length Mismatch:", clen

    if file == "-":
        fp.seek(0)
        print "===============================\n%s\n===============================" % fp.read()
    elif file:
        print "Wrote %s bytes to file: %s" % (clen, file)
    else:
        print "Got %s bytes of content. Use -f <file> if you want to see it." % clen
    fp.close()
sess.dump_gmt_usage()