#!/opt/local/bin/python

import json
import os
import sys

resultsFolder = sys.argv[1]
files = os.listdir(resultsFolder)

filesField = 'files'
statusField= 'status'

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
	if (jsonString == ''):
		jsonString = '{}'
	data = json.loads(jsonString)
	return data

def countConflicts(data):
	conflicts = 0
	ok = 0
	failures = 0
	submodule = 0
	for k in data.keys():
		result = data[k]
		#dealing with old results format
		if (not (statusField in data[k].keys())):
			print('0,0,0')
			return
		if (data[k][statusField] == 'failure'):
			failures = failures + 1
		elif (data[k][statusField] == 'conflict'):
			conflicts = conflicts +1
		elif (data[k][statusField] == 'clean'):
			ok  = ok + 1
		elif (data[k][statusField] == 'submodule'):
			submodule = submodule + 1
	print(str(conflicts) + ',' + str(ok) + "," + str(failures))

print('Project, Conflicts, OK, Failures')
for file in files:
	if shouldIgnore(file):
		continue
	data = loadJson(file)
	countConflicts(data)

