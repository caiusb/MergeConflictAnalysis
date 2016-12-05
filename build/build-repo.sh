#!/bin/bash

if [ -e $1 ]
then
    cd $1
else
    echo "Could not find repo"
    exit 
fi


project=$2
tmpFolder=/scratch/brindesc/$project

mkdir -p $tmpFolder

git checkout -f master > /dev/null 2>&1

if [ -e "pom.xml" ]
then
	buildCmd="mvn compile"
	testCmd="mvn test"
        cleanCmd="mvn clean"
elif [ -e "build.gradle" ]
then
	buildCmd="gradle build"
	testCmd="gradle test"
        cleanCmd="gradle clean"
elif [ -e "build.xml" ]
then
    echo "This uses ant. Results below might not be valid"
    buildCmd="ant build"
    testCmd="ant test"
    cleanCmd="ant clean"
else
	echo "Unknown build system"
        exit
fi

function doBuild() {
    local outputFile=$1
    $buildCmd > $outputFile 2>&1
    if [ $? -ne 0 ]
    then
        local status="build"
    else
        $testCmd > $outputFile 2>&1
        if [ $? -ne 0 ]
        then
                local status="test"
        else
                local status="clean"
        fi
    fi
    $cleanCmd > /dev/null 2>&1
    echo $status
}

git log --merges --format="%H" | while read sha
do
	parents=`git show $sha --format="%P" --quiet`
	p1=`echo $parents | cut -d" " -f1`
	p2=`echo $parents | cut -d" " -f2`
	git checkout -f $p1 > /dev/null 2>&1
	git merge $p2 > /dev/null 2>&1
	if [ $? -ne 0 ]
	then
 		status="conflict"
 	else
                status=$(doBuild $tmpFolder/$sha-test.txt)
 	fi
 	echo "$sha,$status"
done

scp -r $tmpFolder babylon01.eecs.oregonstate.edu:/scratch/brindesc/build-output/$project > /dev/null 2>&1

