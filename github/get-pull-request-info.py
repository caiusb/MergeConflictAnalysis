#!/opt/local/bin/python

import common as c
import os
import json

def getCommitsForPullReq(jsonPull, auth):
	number = pull['number']
	url = pull['_links']['commits']['href']
	resp = c.doApiCall(url, auth)
	return resp

def getEventsForPullReq(jsonPull, auth):
	number = pull['number']
	issueURL = pull['_links']['issue']['href']
	eventsURL = issueURL + '/events'
	resp = c.doApiCall(eventsURL, auth=auth)
	return resp

def getFullPullRequest(jsonPull, auth):
	number = pull['number']
	url = pull['_links']['self']['href']
	resp = c.doApiCall(url, auth=auth)
	return resp

username = c.getUsername()
password = c.getAuthToken()
repos = c.getRepos()
results = c.getResultsFolder() + '/pull-requests'
auth = (username, password)

for repo in repos:
	repoName = repo['repo']
	c.printWithTimeStamp('Getting pull requests for ' + repoName)
	c.printRemainingRateLimit(auth)
	repoRoot = c.getRepoRoot(repo)
	apiCall = repoRoot + '/pulls'
	params = {'state': 'all'}
	listOfPulls = c.doPaginatedApiCall(apiCall, auth=auth, params=params)
	if not os.path.exists(results + "/" + repoName):
		os.mkdir(results + "/" + repoName)
	for pull in listOfPulls:
		pathRoot = results + "/" + repoName
		commitsFile = str(pull['number']) + '.commits.json'
		if not os.path.exists(pathRoot + '/' + commitsFile):
			commits = getCommitsForPullReq(pull, auth)
			c.writeToFile(pathRoot, commitsFile, commits)
		eventsFile = str(pull['number']) + '.events.json'
		if not os.path.exists(pathRoot + '/' + eventsFile):
			events = getEventsForPullReq(pull, auth)
			c.writeToFile(pathRoot, eventsFile, events)
		fullFile = str(pull['number']) + '.full.json'
		if not os.path.exists(pathRoot + '/' +  fullFile):
			fullPull = getFullPullRequest(pull, auth)
			c.writeToFile(pathRoot, fullFile, fullPull)
	c.writeToFile(results + "/" + repoName, "pulls.json", c.getTextFromJson(listOfPulls))
