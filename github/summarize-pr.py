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
		writer.writerow(['PR','MERGEABLE', 'MERGED', 'CLOSED', 'CREATED_TIME', 'MERGED_TIME', 'SHA'])
		processPullRequests(folder, pulls, writer)
		
def processPullRequests(folder, pulls, csvWriter):
	for pull in pulls:
		number = pull['number']
		eventsFile = str(number) + '.events.json'
		commitsFile = str(number) + '.commits.json'
		fullFile = str(number) + '.full.json'
		with open(folder + '/' + eventsFile, 'r') as f:
			eventsContent = f.read()
		with open(folder + '/' + fullFile, 'r') as f:
			fullPullContent = f.read()
		events = json.loads(eventsContent)
		fullPull = json.loads(fullPullContent)
		mergeTime = 0
		createTime = 0
		merged = False
		closed = (fullPull['state'] == 'closed')
		sha = ''
		mergeable = fullPull['mergeable']
		for event in events:
			if (event['event'] == 'merged'):
				mergeDate = event['created_at']
				mergeTime = convertDateToUNIX(mergeDate)
				createDate = fullPull['created_at']
				createTime = convertDateToUNIX(createDate)
				merged = True
				sha = event['commit_id']
		csvWriter.writerow([str(number), str(mergeable), str(merged), str(closed), str(createTime), str(mergeTime), sha])

def convertDateToUNIX(date):
	return datetime.datetime.strptime(date, '%Y-%m-%dT%H:%M:%SZ').strftime('%s')

projects = os.listdir(resultsFolder)
for project in projects:
	if (re.search('^\.', project) != None):
		continue
	getProjectData(resultsFolder + "/" + project)

