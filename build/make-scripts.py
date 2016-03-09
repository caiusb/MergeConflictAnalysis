#!~/.local/bin/python

import os
import subprocess as s

repos = [line.rstrip('\n') for line in open('../repos-buildable.txt')]

shebang = "#!/bin/bash\n\n"
wd="wd=$HOME/merging/workspace/\n"

jar = "$wd/MergeConflictAnalysis-assembly-1.0.0.jar"

s.call("./make-dir-structure.sh")

for repo in repos:
	repoName = repo.split("/")[-1]
	with open("scripts/" + repoName + ".sh", "w") as script:
		script.write(shebang)
		script.write(wd)
		script.write("cd $wd\n")
		script.write("pwd\n\n")
		script.write("export PATH=$HOME/jdk8/bin:$HOME/maven/bin:$HOME/.local/bin:$PATH\n")
		script.write("export M2_HOME=`mvn --version | grep \"Maven home\" | rev | cut -d':' -f1 | rev | sed \"s/^ *//\"`\n\n")
		script.write("tmpdir=\"/scratch/brindesc/$JOB_ID/\"\n\n")
		script.write("if [ ! -d \"$tmpdir\" ]\n")
		script.write("then\n")
		script.write("\tmkdir -p \"$tmpdir\"\n")
		script.write("fi\n\n")
		script.write("pushd \"$tmpdir\" > /dev/null \n")
		script.write("WAIT=$RANDOM\n")
		script.write("let \"WAIT %= 600\"\n")
		script.write("echo \"Waiting for $WAIT seconds\"\n")
		script.write("sleep $WAIT\n")
		script.write("git clone " + repo + "\n")
		script.write("popd >/dev/null \n\n")
		script.write("java -Xmx2G -jar " + jar + " -output=\"../build-data/results/" + repoName + ".csv\"" " -build -log-to-console " + "$tmpdir/" + repoName + "\n\n")
		script.write("rm -rf \"$tmpdir\"\n\n")