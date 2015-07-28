#!/opt/local/bin/python

import requests as req

username = 'caiusb'
passwordFile = 'token'
reposFile = 'repos.txt'
root = 'https://api.github.com/repos/'
results = 'results'

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

password = getAuthToken(passwordFile)
repos = getRepos(reposFile)

def writeToFile(folder, fileName, content):
	f = open(folder + '/' + fileName, 'w')
	f.write(content)
	f.close()

for repo in repos:
	print('Getting pull requests for ' + repo['repo'])
	apiCall = root + repo['username'] + '/' + repo['repo'] + '/pulls'
	resp = req.get(apiCall, auth=(username, password))
	if (resp.status_code != 200):
		print('Error getting data for ' + repo['repo'])
	writeToFile(results, repo['repo'] + ".json", resp.text)
