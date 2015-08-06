#!/opt/local/bin/python

import os
import csv

authorsFolder = '../../results/authors'
mergeFolder = '../../results/per-commit/'

def getSetOfMergeCommits(mergeFolder):
	merges = set()
	files = os.listdir(mergeFolder)
	for f in files:
		projectName = f.split('.')[0]
		filePath = mergeFolder + '/' + f
		if (os.path.isdir(filePath)):
			continue
		with open(filePath, 'r') as csvfile:
			reader = csv.DictReader(csvfile)
			for row in reader:
				merges.add(row['SHA'])
	return merges

merges = getSetOfMergeCommits(mergeFolder)

commitsPerProject = dict()
totalNoAuthors = 0
totalNoMergers = 0
totalCommitsByMergers = 0
totalCommits = 0

files = os.listdir(authorsFolder)
print('PROJECT,NO_MERGERS,NO_AUTHORS,NO_COMMITS,NO_COMMITS_MERGERS')
for f in files:
	mergeAuthors = set()
	allAuthors = set()
	commitsByAuthor = dict()
	filePath = authorsFolder + '/' + f
	if (os.path.isdir(filePath)):
		continue
	with open(filePath, 'r', encoding='utf-8', errors='ignore') as csvFile:
		reader = csv.DictReader(csvFile)
		projectName = f.split('.')[0]
		commitsPerProject[projectName] = 0
		for row in reader:
			sha = row['SHA']
			author = row['AUTHOR']
			commitsPerProject[projectName] = commitsPerProject[projectName] + 1
			if (sha in merges):
				mergeAuthors.add(author)
			allAuthors.add(author)
			#Count commit for the author
			if (author in commitsByAuthor):
				commitsByAuthor[author] = commitsByAuthor[author] + 1
			else:
				commitsByAuthor[author] = 1
	#Variables
	noOfMergers = len(mergeAuthors)
	noOfAuthors = len(allAuthors)
	noOfCommits = 0
	commitsByMergers = 0
	totalNoAuthors += noOfAuthors
	totalNoMergers += noOfMergers
	#Safe get of project commits
	if (projectName in commitsPerProject):
		projectCommits = commitsPerProject[projectName]
	else:
		projectCommits = 0
	#Count commits made by mergers
	for author in mergeAuthors:
		commitsByMergers += commitsByAuthor[author]

	#Count totals
	totalCommitsByMergers += commitsByMergers
	totalCommits += projectCommits
	print(projectName + ',' + str(noOfMergers) + ',' + str(noOfAuthors) + ',' + str(projectCommits) + ',' + str(commitsByMergers))

print("Total," + str(totalNoMergers) + ',' + str(totalNoAuthors)  + ',' + str(totalCommits) + ',' + str(totalCommitsByMergers))
