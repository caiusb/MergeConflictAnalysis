#!/opt/local/bin/python

import json
import os
import sys

def loadJson(file):
	jsonString = ''
	with open(file, 'r') as f:
		for line in f:
			if not line.startswith('Error checking out'):
				jsonString += line
	if (jsonString == ''):
		jsonString = '{}'
	data = json.loads(jsonString)
	return data

def shouldIgnore(file):
	return not file.endswith('.json')

def processJSONFilesInFolder(folder, processFunction):
	files = os.listdir(folder)
	for file in files:
		if (shouldIgnore(file)):
			continue;
		data = loadJson(os.path.join(folder,file))
		try:
			processFunction(file, data)
		except:
			print("Error processing " + file)