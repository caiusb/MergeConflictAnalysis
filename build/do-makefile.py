#!/opt/local/bin/python

import subprocess as s
import os
import git as g
import sys

repoFolder = '/scratch/brindesc/git'

if (len(sys.argv) == 2):
	repoFolder = sys.argv[1]

testCommand = ['make', 'test']
buildCommand = 'make'
cleanCommand = ['make', 'clean']

FNULL = open(os.devnull, 'w')

class BuildResult:
	def __init__(self, parent1, parent2, merge):
		self.parent1 = parent1
		self.parent2 = parent2
		self.merge = merge

	def __str__(self):
		return self.getResult()

	def getResult(self):
		return self.parent1 + ',' + self.parent2 + ',' + self.merge

def buildAsIs(hexsha, repo):
	repo.git.checkout(hexsha, f=True)
	return build()

def build():
	s.call(cleanCommand, stdout=FNULL, stderr=FNULL)
	if (s.call(buildCommand, stdout=FNULL, stderr=FNULL) != 0):
		return "build"
	elif (s.call(testCommand, stdout=FNULL, stderr=FNULL) != 0):
		return "test"
	else:
		return "pass"

def writeToFile(hexsha, BuildResult):
	with open(os.path.expanduser("~/merging/build-data/perl.csv"), "a") as f:
		f.write(hexsha + "," + str(results[hexsha]) + "\n")


results = {}
builds = {}

os.chdir(repoFolder)

repo = g.Repo(repoFolder)
commits = repo.iter_commits(repo.branches.blead)
merges = [c for c in commits if len(c.parents) == 2]
merges.sort(key = lambda x: x.authored_date + x.author_tz_offset)

for m in merges:
	print('Testing ' + m.hexsha + " [" +  str(merges.index(m) + 1) + "/" + str(len(merges)) + "]")
	hexsha = m.hexsha
	p1 = m.parents[0].hexsha
	p2 = m.parents[1].hexsha
	if (not p1 in builds):
		builds[p1] = buildAsIs(p1, repo)
	if (not p2 in builds):
		builds[p2] = buildAsIs(p2, repo)
	s.call(cleanCommand, stdout=FNULL, stderr=FNULL)
	repo.git.checkout(p2, f=True)
	try:
		repo.git.merge(p1)
	except g.exc.GitCommandError:
		results[hexsha] = BuildResult(builds[p1], builds[p2], "text")
		writeToFile(hexsha, results[hexsha])
		continue
	results[hexsha] = BuildResult(builds[p1], builds[p2], build())
	writeToFile(hexsha, results[hexsha])
	repo.git.checkout(".", f=True)
	
print(merges)
