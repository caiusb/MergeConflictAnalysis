#!/opt/local/bin/python

import pymongo
import common
import os
import sys

resultsFolder = sys.argv[1]

client = MongoClient()
db = client.development

def putDataInMongo(file, data) {
	projectName = os.path.splitext(file)
	collection = db['projectName']
	collection.insert_many(data)
}

common.processJSONFilesInFolder(resultsFolder, putDataInMongo)