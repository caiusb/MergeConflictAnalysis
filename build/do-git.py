#!/opt/local/bin/python

import subprocess as s
import os
import git as g

gitFolder = '/scratch/brindesc/git'

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
	repo.git.checkout(hexsha)
	return build()

def build():
	s.call(cleanCommand, stdout=FNULL, stderr=FNULL)
	if (s.call(buildCommand, stdout=FNULL, stderr=FNULL) != 0):
		return "build"
	elif (s.call(testCommand, stdout=FNULL, stderr=FNULL) != 0):
		return "test"
	else:
		return "pass"

results = {}
builds = {}

os.chdir(gitFolder)

repo = g.Repo(gitFolder)
commits = repo.iter_commits(repo.branches.master)
merges = [c for c in commits if len(c.parents) == 2]

for m in merges:
	print('Testing ' + m.hexsha + " [" +  str(merges.index(m)) + "/" + str(len(merges)) + "]")
	hexsha = m.hexsha
	p1 = m.parents[0].hexsha
	p2 = m.parents[1].hexsha
	if (not p1 in builds):
		builds[p1] = buildAsIs(p1, repo)
	if (not p2 in builds):
		builds[p2 ] = buildAsIs(p2, repo)
	s.call(cleanCommand, stdout=FNULL, stderr=FNULL)
	repo.git.checkout(p2)
	try:
		repo.git.merge(p1)
	except GitCommandError:
		results[hexsha] = BuildResult(builds[p1], builds[p2], "text")
		continue
	results[hexsha] = BuildResult(builds[p1], builds[p2], build())
	print(results[hexsha])
	print(type(builds[p1]))
	repo.git.checkout(".", f=True)
	with open(os.path.expanduser("~/merging/build-data/git.csv"), "w+") as f:
		f.write(hexsha + "," + builds[p1] + "," + builds[p2] + "," + results[hexsha] + "\n")

print(merges)
