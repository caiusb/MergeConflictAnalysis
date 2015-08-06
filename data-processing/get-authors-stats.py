#!/opt/local/bin/python

import os
import csv

authorsFolder = '../../results/authors'
mergeFolder = '../../results/per-commit/'

def getSetOfMergeCommits(mergeFolder):
	merges = set()
	files = os.listdir(mergeFolder)
	for f in files:
		filePath = mergeFolder + '/' + f
		if (os.path.isdir(filePath)):
			continue
		with open(filePath, 'r') as csvfile:
			reader = csv.DictReader(csvfile)
			for row in reader:
				merges.add(row['SHA'])
	return merges

merges = getSetOfMergeCommits(mergeFolder)

totalNoAuthors = 0
totalNoMergers = 0

files = os.listdir(authorsFolder)
print('PROJECT,NO_MERGERS,NO_AUTHORS')
for f in files:
	mergeAuthors = set()
	allAuthors = set()
	filePath = authorsFolder + '/' + f
	if (os.path.isdir(filePath)):
		continue
	with open(filePath, 'r', encoding='utf-8', errors='ignore') as csvFile:
		reader = csv.DictReader(csvFile)
		projectName = f.split('.')[0]
		for row in reader:
			sha = row['SHA']
			author = row['AUTHOR']
			if (sha in merges):
				mergeAuthors.add(author)
			allAuthors.add(author)
	noOfMergers = len(mergeAuthors)
	noOfAuthors = len(allAuthors)
	totalNoAuthors += noOfAuthors
	totalNoMergers += noOfMergers
	print(projectName + ',' + str(noOfMergers) + ',' + str(noOfAuthors))

print("Total," + str(totalNoMergers) + ',' + str(totalNoAuthors))
