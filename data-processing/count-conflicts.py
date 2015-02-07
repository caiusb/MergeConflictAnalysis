#!/usr/bin/python

import json
import os
import sys

resultsFolder = sys.argv[1]
files = os.listdir(resultsFolder)

def shouldIgnore(file):
	return not file.endswith('.json')	

for file in files:
	if shouldIgnore(file):
		continue
	pathToFile = os.path.join(resultsFolder,file)
	print(pathToFile)
	jsonString = ''
	with open(pathToFile, 'r') as f:
		for line in f:
			if not line.startswith('Error checking out'):
				jsonString += line
		data = json.loads(jsonString)

	# do the dew
