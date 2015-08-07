#!/opt/local/bin/python

import os
import requests as req

username = 'caiusb'
passwordFile = 'token'
reposFile = 'repos.txt'
root = 'https://api.github.com/repos/'
results = '../../results'

def doApiCall(url, auth, params={}):
	resp = req.get(url, auth=auth, params=params)
	if (resp.status_code == 403):
		while (resp.headers['X-RateLimit-Remaining'] == 0):
			sleepTime = r.headers['X-RateLimit-Reset']
			print('Exhausted the API Rate Limit. Sleeping for ' + str(sleepTime))
			sleep(sleepTime)
			resp = req.get(url, auth=auth, params=params)
	return resp.text

def getUsername():
	return username

def getAuthToken():
	f = open(passwordFile, 'r')
	password = f.read()[:-1]
	f.close()
	return password

def getRepos():
	f = open(reposFile, 'r')
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
	with open(folder + '/' + fileName, 'w') as f:
		f.write(content)

def getApiRoot():
	return root

def getResultsFolder():
	return results

def getRepoRoot(repo):
	return getApiRoot() + repo['username'] + '/' + repo['repo']
