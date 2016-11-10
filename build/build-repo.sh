#!/bin/bash

cd $1

git checkout -f master > /dev/null 2>&1

if [ -e "pom.xml" ]
then
	buildCmd="mvn build"
	testCmd="mvn test"
elif [ -e "build.gradle" ]
then
	buildCmd="gradle build"
	testCmd="gradle test"
else
	echo "Unknown build system"
fi

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
 		$buildCmd > /dev/null 2>&1
 		if [ $? -ne 0 ]
 		then
 			status="build"
 		else
 			$testCmd > /dev/null 2>&1
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

