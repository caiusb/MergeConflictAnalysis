import os
import json

jsonFolder = "../../data/merge-lines"
csvFile = "../../data/merge-lines.csv"
jsonFiles = [ x for x in os.listdir(jsonFolder) if x.endswith(".json") ]

csv = open(csvFile, 'w')
csv.write("project,sha,file,line\n")

for file in jsonFiles:
	print(file)
	filePath = os.path.join(jsonFolder, file)
	if (os.stat(filePath).st_size == 0):
		continue
	with open(filePath) as f:
		j = json.load(f)
	project = os.path.splitext(file)[0]
	for commit in j:
		sha = commit['sha']
		files = [ x for x in commit['files'] if x['name'].endswith('.java') ]
		for file in files:
			fileName = file['name']
			lines = file['lines']
			for line in lines:
				csv.write(project + "," + sha + "," + fileName + "," + str(line) + "\n")

csv.close()
