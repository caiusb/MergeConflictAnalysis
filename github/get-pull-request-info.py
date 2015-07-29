#!/opt/local/bin/python

import requests as req
import json
import os

username = 'caiusb'
passwordFile = 'token'
reposFile = 'repos.txt'
root = 'https://api.github.com/repos/'
results = '../../results/pull-requests'

def getAuthToken(file):
	f = open(file, 'r')
	password = f.read()[:-1]
	f.close()
	return password

def getRepos(file):
	f = open(file, 'r')
	string = f.read()
	f.close()

	listOfRepos = string.split('\n')
	repos = []
	for repo in listOfRepos:
		if (repo == ''):
			continue
		rs = repo.split(',')
		repos.append({'username': rs[0], 'repo': rs[1]})

	return repos

def writeToFile(folder, fileName, content):
	f = open(folder + '/' + fileName, 'w')
	f.write(content)
	f.close()

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

password = getAuthToken(passwordFile)
repos = getRepos(reposFile)

for repo in repos:
	repoName = repo['repo']
	print('Getting pull requests for ' + repoName)
	repoRoot = root + repo['username'] + '/' + repoName
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
		writeToFile(pathRoot, str(pull['number']) + '.commits.json', commits)
		events = getEventsForPullReq(pull, auth)
		writeToFile(pathRoot, str(pull['number']) + '.events.json', events) 
	writeToFile(results + "/" + repoName, "pulls.json", text)
