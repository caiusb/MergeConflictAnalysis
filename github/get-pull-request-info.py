#!/opt/local/bin/python

import common as c
import requests as req
import json
import os

def getCommitsForPullReq(jsonPull, auth):
	number = pull['number']
	url = pull['_links']['commits']['href']
	resp = req.get(url, auth=auth)
	return resp.text

def getEventsForPullReq(jsonPull, auth):
	number = pull['number']
	issueURL = pull['_links']['issue']['href']
	eventsURL = issueURL + '/events'
	resp = req.get(eventsURL, auth=auth)
	return resp.text

username = c.getUsername()
password = c.getAuthToken()
repos = c.getRepos()
results = c.getResultsFolder()

for repo in repos:
	repoName = repo['repo']
	print('Getting pull requests for ' + repoName)
	repoRoot = c.getApiRoot() + repo['username'] + '/' + repoName
	apiCall = repoRoot + '/pulls'
	params = {'state': 'all'}
	auth = (username, password)
	resp = req.get(apiCall, auth=auth, params=params)
	if (resp.status_code != 200):
		print('Error getting data for ' + repoName)
	text = resp.text
	listOfPulls = json.loads(text)
	os.mkdir(results + "/" + repoName)
	for pull in listOfPulls:
		pathRoot = results + "/" + repoName
		commits = getCommitsForPullReq(pull, auth)
		c.writeToFile(pathRoot, str(pull['number']) + '.commits.json', commits)
		events = getEventsForPullReq(pull, auth)
		c.writeToFile(pathRoot, str(pull['number']) + '.events.json', events) 
	writeToFile(results + "/" + repoName, "pulls.json", text)
