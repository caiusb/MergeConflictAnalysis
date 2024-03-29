#!/opt/local/bin/python

import os
import sys
import git as g
import subprocess as s
from functools import reduce
import json

def getChangedLines(repo, sha):
	toCall = "git diff " + sha + " " + sha + "~1 -U0 | grep -E \"(@@)|(\+\+\+)\""
	try:
		output = s.check_output(toCall, shell=True)
	except s.CalledProcessError:
		return {}
	dict = {}
	currentFile = ""
	ranges = []
	for line in output.decode('utf-8').split('\n'):
		if (line.startswith("+++")):
			if (currentFile != ""):
				dict[currentFile] = ranges
				currentFile = line.split(" ")[1].split("/",1)[1]
				ranges = []
			else:
				currentFile = line.split(" ")[1].split("/",1)[1]
		elif (line.startswith("@@")):
			ranges.append(line.split(" ")[2])

	return dict

repoPath = sys.argv[1]
os.chdir(repoPath)
#print(os.getcwd())
repo = g.Repo(".")
merges = [c for c in repo.iter_commits(repo.branches.master) if len(c.parents) >= 2 ]

bigDict = {}

for m in merges:
	lines = getChangedLines(repo, m.hexsha)
	newLines = {}
	for file in lines:
		ranges = []
		for r in lines[file]:
			bits = r.split(',')
			if (len(bits) == 1):
				ranges.append(range(int(bits[0]),int(bits[0])+1))
			else:
				if(bits[1] != '0'):
					ranges.append(range(int(bits[0]), int(bits[0])+int(bits[1])))
		ranges = list(reduce(set.union,[list(r) for r in ranges],set()))
		newLines[file]=ranges
	bigDict[m.hexsha] = newLines

a = []
for commit in bigDict.keys():
	files = []
	for file in bigDict[commit]:
		files.append({'name': file, 'lines': bigDict[commit][file]})
	newDict = {'sha': commit, 'files': files}
	a.append(newDict)

print(json.dumps(a, indent=3, separators=(',',':')))


	
