#!/opt/local/bin/python

import json
import csv
import re
import os
import time
import datetime

resultsFolder = '../../results/pull-requests'

def getProjectData(folder):
	pullFile = folder + '/pulls.json'
	with open(pullFile, 'r') as f:
		pullContent = f.read()
	pulls = json.loads(pullContent)
	with open(folder + '/summary.csv', 'w') as csvFile:
		writer = csv.writer(csvFile, delimiter=',', quoting=csv.QUOTE_MINIMAL)
		writer.writerow(['PR', 'MERGED', 'CLOSED', 'CREATED_TIME', 'MERGED_TIME', 'SHA'])
		processPullRequests(folder, pulls, writer)
		
def processPullRequests(folder, pulls, csvWriter):
	for pull in pulls:
		number = pull['number']
		eventsFile = str(number) + '.events.json'
		commitsFile = str(number) + '.commits.json'
		with open(folder + '/' + eventsFile, 'r') as f:
			eventsContent = f.read()
		events = json.loads(eventsContent)
		mergeTime = 0
		createTime = 0
		merged = False
		if (pull['state'] == 'closed'):
			closed = True
		else:
			closed = False
		sha = ''
		for event in events:
			if (event['event'] == 'merged'):
				mergeDate = event['created_at']
				mergeTime = convertDateToUNIX(mergeDate)
				createDate = pull['created_at']
				createTime = convertDateToUNIX(createDate)
				merged = True
				sha = event['commit_id']
		csvWriter.writerow([str(number), str(merged), str(closed), str(createTime), str(mergeTime), sha])

def convertDateToUNIX(date):
	return datetime.datetime.strptime(date, '%Y-%m-%dT%H:%M:%SZ').strftime('%s')

projects = os.listdir(resultsFolder)
for project in projects:
	if (re.search('^\.', project) != None):
		continue
	getProjectData(resultsFolder + "/" + project)

