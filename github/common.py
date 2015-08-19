#!/opt/local/bin/python

import os
import time
import requests as req
import re
import json
import datetime

username = 'caiusb'
passwordFile = 'token'
reposFile = 'new_repos.txt'
root = 'https://api.github.com/repos/'
results = '../../results'

def printWithTimeStamp(text):
	today = datetime.datetime.today()
	print('[' + str(today) + ']: ' + text)

def doPaginatedApiCall(url, auth, params={}):
	resp = doRawApiCall(url, auth=auth, params=params)
	jsonList = json.loads(resp.text)
	nextUrl = getNextURL(resp)
	if (nextUrl is not None):
		jsonList.extend(doPaginatedApiCall(nextUrl, auth=auth, params=params))
	return jsonList

def doApiCall(url, auth, params={}):
	resp = doRawApiCall(url, auth, params=params)
	return resp.text

def doRawApiCall(url, auth, params={}):
	resp = req.get(url, auth=auth, params=params)
	if (resp.status_code == 403):
		while resp.headers['X-RateLimit-Remaining'] == '0':
			resetTime = float(resp.headers['X-RateLimit-Reset'])
			sleepTime = resetTime - time.time()
			if sleepTime > 0:
				printWithTimeStamp('Exhausted the API Rate Limit. Sleeping for ' + str(sleepTime))
				time.sleep(sleepTime)
			resp = req.get(url, auth=auth, params=params)
		printWithTimeStamp("Resuming...")
	return resp

def getNextURL(resp):
	if (not 'Link' in resp.headers):
		return None
	linksText = resp.headers["Link"]
	links = linksText.split(',')
	for link in links:
		if 'rel=\"next\"' in link:
			url = re.sub('<', '', re.sub('>', '', link.split(';')[0]))
			return url
	return None

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

def printRemainingRateLimit(auth):
	text = doApiCall('https://api.github.com/rate_limit', auth=auth)
	limit = json.loads(text)
	printWithTimeStamp('Remaining api calls: ' + str(limit['rate']['remaining']))

def getTextFromJson(jsonDict):
	return json.dumps(jsonDict, separators=(',',':'))

def repoExists(repoApiCall, auth):
	resp = doRawApiCall(repoApiCall, auth=auth)
	if (resp.status_code == 404):
		return False
	else:
		return True
