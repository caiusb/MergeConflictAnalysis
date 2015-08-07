#!/opt/local/bin/python

import common as c
import json
import os
import csv

eventsFolder = c.getResultsFolder() + '/events'
eventsFiles = os.listdir(eventsFolder)
for eventFile in eventsFiles:
	with open(eventsFolder + '/' + eventFile, 'r') as f:
		events = json.load(f)
	pushes = [ x for x in events if x['type'] == 'PushEvent' ]

authorsFolder = c.getResultsFolder() + '/authors'
authorsFiles = os.listdir(authorsFolder)
commits = list()
for authorsFile in authorsFiles:
	with open(authorsFolder + '/' + authorsFile, 'r', errors='ignore') as f:
		authors = csv.DictReader(f)
		for row in authors:
			commits.append(row['SHA'])

missingCommits = []
for push in pushes:
	pushedCommits = push['payload']['commits']
	for commit in pushedCommits:
		sha = commit['sha']
		if (not sha in commits):
			missingCommits.append(sha)

pullFolder = c.getResultsFolder() + '/pull-requests'
pullFiles = os.listdir(pullFolder)
for folder in pullFiles:
	with open(pullFolder + '/' + folder + '/pulls.json') as f:
		pulls = csv.DictReader(f)
		for pull in pulls:
			head = pull['head']['sha']
			if (head in missingCommits):
				print('Pull request was rebased: ' + pull['number'])
