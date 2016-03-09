#!/opt/local/bin/python

import subprocess as s
import os
import git as g

gitFolder = '../'#'/scratch/brindesc/git'

testCommand = 'make test'
buildCommand = 'make'
cleanCommand = 'make clean'

class BuildResult:
	def __init__(self, parent1, parent2, merge):
		self.parent1 = parent1
		self.parent2 = parent2
		self.parent3 = parent3

	def getResult(self):
		return self.parent1 + ',' + self.parent2 + ',' + self.parent3

def buildAsIs(hexsha, repo):
	repo.git.checkout(hexsha)
	s.call(cleanCommand)
	build

def build():
	if (s.call(buildCommand) != 0)
			return "build"
		elif (s.call(testCommand) != 0):
			return "test"
		else:
			return "pass"

builds = {}
results = {}

os.chdir(gitFolder)

repo = g.Repo(gitFolder)
commits = repo.iter_commits(repo.branches.master)
merges = [c for c in commits if len(c.parents) == 2]

for m in merges:
	hexsha = m.hexsha
	p1 = m.parents[0].hexsha
	p2 = m.parents[1].hexsha
	if (not p1 in builds):
		builds[p1]=buildAsIs(p1)
	if (not p2 in builds):
		builds[p2]=buildAsIs(p2)
	s.call(cleanCommand)
	repo.git.checkout(p2)
	try:
		repo.git.merge(p1)
	except GitCommandError:
		merges[hexsha] = BuildResult(builds[p1], builds[p2], "text")
		continue
	merges[hexsha] = BuildResult(builds[p1], builds[2], build)
	r.git.checkout(".", f=True)