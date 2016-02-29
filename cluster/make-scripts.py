#!~/.local/bin/python

import os
import subprocess as s

repos = [line.rstrip('\n') for line in open('../repos-buildable.txt')]

shebang = "#!/bin/bash\n\n"
jar = "$HOME/merging/MergeConflictAnalysis-assembly-1.0.0.jar"
jobOps = "-cwd"

s.call("./make-dir-structure.sh")

for repo in repos:
	repoName = repo.split("/")[-1]
	with open("scripts/" + repoName + ".sh", "w") as script:
		script.write(shebang)
		script.write("tmpdir=\"/scratch/brindesc\"\n\n")
		script.write("if [ ! -d \"$tmpdir\" ]\n")
		script.write("then\n")
		script.write("\tmkdir \"$tmpdir\"\n")
		script.write("fi\n\n")
		script.write("pushd \"$tmpdir\" > /dev/null \n")
		script.write("git clone " + repo)
		script.write("popd >/dev/null \n\n")
		script.write("java -jar " + jar + " -Xmx1G " + "-output=../../build-data/results/" + repoName + ".csv " + "/scratch/brindesc/" + repoName + "\n\n")
		script.write("rm -rf \"/$tmpdir\"\n\n")