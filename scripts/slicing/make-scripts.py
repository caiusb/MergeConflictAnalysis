#!~/.local/bin/python

import os
import subprocess as s

repos = [line.rstrip('\n') for line in open('../../repos-buildable.txt')]

shebang = "#!/bin/bash\n\n"
wd = "wd=$HOME/merging/slicer/\n"

jar = "$wd/slicer-assembly-0.1-SNAPSHOT.jar"

for repo in repos:
    repoName = repo.split("/")[-1]
    with open("scripts/" + repoName + ".sh", "w") as script:
        script.write(shebang)
        script.write(wd)
        script.write("cd $wd\n")
        script.write("mkdir ../../slicer-results\n")
        script.write("pwd\n\n")
        script.write(
            "export PATH=$HOME/jdk8/bin:$HOME/maven/bin:$HOME/.local/bin:$PATH\n")
        script.write(
            "export M2_HOME=`mvn --version | grep \"Maven home\" | rev | cut -d':' -f1 | rev | sed \"s/^ *//\"`\n\n")
        script.write("tmpdir=\"/scratch/brindesc/$JOB_ID/\"\n")
        script.write("mavenCache=\"/scratch/brindesc/m2/repository\"\n\n")
        script.write("if [ ! -d \"$tmpdir\" ]\n")
        script.write("then\n")
        script.write("\tmkdir -p \"$tmpdir\"\n")
        script.write("fi\n\n")
        script.write("if [ ! -d \"$mavenCache\" ]\n")
        script.write("then\n")
        script.write("\tmkdir -p \"$mavenCache\"\n")
        script.write("fi\n\n")
        script.write("pushd \"$tmpdir\" > /dev/null \n")
        script.write("rsync -avz babylon01.eecs.oowdregonstate.edu:/scratch/brindesc/ase16-repos/" + repoName + " .\n")
        script.write("rsync -avz babylon01.eecs.oregonstate.edu:/scratch/brindesc/merge-lines/" + repoName + ".json . \n")
        script.write("if [ ! -e " + repoName + "/pom.xml ]\nthen\n")
        script.write("\trm -rf \"$tmpdir\"\n")
        script.write("\trm -rf \"$mavenCache\"\n")
        script.write("\texit\n")
        script.write("fi\n\n")
    	script.write("pwd\n")
    	script.write("ls\n")
        script.write("popd >/dev/null \n\n")
        script.write("java -Xmx28G -jar " + jar + "\"$tmpdir/" + repoName + " .json\" \"$tmpdir/" + repoName + "\n ../../slicer-results/" + repoName + ".csv \n")
        script.write("echo \"Analysis is done for " + repoName + " with status $?\"\n\n")
        script.write("rm -rf \"$tmpdir\"\n")
        script.write("rm -rf \"$mavenCache\"\n")
