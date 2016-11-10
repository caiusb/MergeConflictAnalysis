#!/bin/bash

cd $1

if [ -e "pom.xml" ]
then
	buildCmd="maven build"
	testCmd="mvn test"
elif [ -e "build.gradle" ]
then
	buildCmd="gradle build"
	testCmd="gradle test"
else
	echo "Unknown build system"
fi


git log --merges | while read sha
do
	parents=`git show $sha --format="%P"`
	p1=`echo $parents | cut -d" " -f1`
	p2=`echo $parents | cut -d" " -f2`
	git checkout -f $p1 > /dev/nul
	git merge $p2 > /dev/null
	if [ $? -ne 0 ]
	then
 		status="conflict"
 	else
 		$buildCmd > /dev/null
 		if [ $? -ne 0]
 		then
 			status="build"
 		else
 			$testCmd > /dev/null
 			if [ $? -ne 0 ]
 			then
 				status="test"
 			else
 				status="clean"
 			fi
 		fi
 	fi
 	echo "$sha,$status"
done
