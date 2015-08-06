#!/opt/local/bin/python

import common as c
import requests as req
import json

username = c.getUsername()
password = c.getAuthToken()
apiRoot = c.getApiRoot()
resultsFolder = c.getResultsFolder() + '/events'

repos = c.getRepos()

for repo in repos:
	repoName = repo['repo']
	print('Getting events for ' + repoName)
	repoRoot = c.getRepoRoot(repo)
	apiCall = repoRoot + "/events"
	auth = (username, password)
	resp = req.get(apiCall, auth=auth)
	if (resp.status_code != 200):
		print('Error getting events for ' + repoName)
	text = resp.text
	c.writeToFile(resultsFolder, repoName + '.json', text)
