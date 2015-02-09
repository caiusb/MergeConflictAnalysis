#!/opt/local/bin/python

import json
import os
import sys

resultsFolder = sys.argv[1]
files = os.listdir(resultsFolder)

def shouldIgnore(file):
	return not file.endswith('.json')

def convertToBool(string):
	if (string == 'failure'):
		return 'failure'
	if (string == 'true'):
		return True
	return False

def loadJson(file):
	print(file + ',', end="")
	pathToFile = os.path.join(resultsFolder,file)
	jsonString = ''
	with open(pathToFile, 'r') as f:
		for line in f:
			if not line.startswith('Error checking out'):
				jsonString += line
		data = json.loads(jsonString)
	data = {k.encode('utf8'): data[k] for k in data.keys()}
	data = {k: {convertToBool(k1): data[k][k1] for k1 in data[k].keys()} for k in data.keys()}
	return data

def countConflicts(data):
	conflicts = 0
	ok = 0
	failures = 0
	for k in data.keys():
		result = data[k]
		if ('failure' in result.keys()):
			failures = failures + 1
		else: 
			if (True in result.keys()):
				conflicts = conflicts +1
			else:
				ok  = ok + 1
	print(str(conflicts) + ',' + str(ok) + "," + str(failures))

print('Project, Conflicts, OK, Failures')
for file in files:
	if shouldIgnore(file):
		continue
	data = loadJson(file)
	countConflicts(data)

