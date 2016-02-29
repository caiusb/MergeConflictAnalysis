#!~/.local/bin/python

import os
import subprocess as s

repos = [line.rstrip('\n') for line in open('../repos-buildable.txt')]

shebang = "#!/bin/bash\n\n"
jar = "$HOME/merging/workspace/MergeConflictAnalysis-assembly-1.0.0.jar"
jobOps = "-cwd"

s.call("./make-dir-structure.sh")

for repo in repos:
	repoName = repo.split("/")[-1]
	with open("scripts/" + repoName + ".sh", "w") as script:
		script.write(shebang)
		script.write("export PATH=$HOME/jdk8/bin:$HOME/maven/bin:$HOME/.local/bin:$PATH\n")
		script.write("export M2_HOME=`mvn --version | grep \"Maven home\" | rev | cut -d':' -f1 | rev | sed \"s/^ *//\"`\n\n")
		script.write("tmpdir=\"/scratch/brindesc\"\n\n")
		script.write("if [ ! -d \"$tmpdir\" ]\n")
		script.write("then\n")
		script.write("\tmkdir \"$tmpdir\"\n")
		script.write("fi\n\n")
		script.write("pushd \"$tmpdir\" > /dev/null \n")
		script.write("git clone " + repo + "\n")
		script.write("popd >/dev/null \n\n")
		script.write("java -jar " + jar + " -Xmx2G " + "-output=../../build-data/results/" + repoName + ".csv -build -log-to-console " + repoName + ".csv " + "/scratch/brindesc/" + repoName + "\n\n")
		script.write("rm -rf \"$tmpdir\"\n\n")