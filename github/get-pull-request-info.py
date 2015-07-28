#!/opt/local/bin/python

import requests as req

username = 'caiusb'
passwordFile = 'token'
reposFile = 'repos.txt'
root = 'https://api.github.com/repos/'

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

for repo in repos:
	apiCall = root + repo['username'] + '/' + repo['repo'] + '/pulls'
	r = req.get(apiCall, auth=(username, password))
	print(r.status_code)
	print(r.text)

