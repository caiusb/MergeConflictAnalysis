#!/opt/local/bin/python

import os

username = 'caiusb'
passwordFile = 'token'
reposFile = 'repos.txt'
root = 'https://api.github.com/repos/'
results = '../../results'

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
	f = open(folder + '/' + fileName, 'w')
	f.write(content)
	f.close()

def getApiRoot():
	return root

def getResultsFolder():
	return results

def getRepoRoot(repo):
	return getApiRoot() + repo['username'] + '/' + repo['repo']
