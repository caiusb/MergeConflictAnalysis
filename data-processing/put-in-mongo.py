#!/opt/local/bin/python

from pymongo import MongoClient
import common
import os
import sys

resultsFolder = sys.argv[1]

client = MongoClient()
db = client.development

def putDataInMongo(file, data) :
	projectName = os.path.splitext(file)
	collection = db[projectName[0]]
	collection.insert(data)

common.processJSONFilesInFolder(resultsFolder, putDataInMongo)