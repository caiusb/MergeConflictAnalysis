#!/opt/local/bin/python

import sys

import common

resultsFolder = sys.argv[1]

filesField = 'files'
statusField= 'status'

def convertToBool(string):
	if (string == 'failure'):
		return 'failure'
	if (string == 'true'):
		return True
	return False

def countConflicts(file, data):
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
common.processJSONFilesInFolder(resultsFolder, countConflicts)

