#!/bin/bash

source common.sh

repoloc=$(resolve-path $1)

repoName="Repo name"
noOfMerges="Merges"
moreThanTwo="Merges 3 parents"

function stat-repo() {
	repoPath=$1
	pushd $repoPath > /dev/null
	merges=`git log --merges --oneline | wc -l | sed 's/^ *//'`
	threeParents=`git log --min-parents=3 --oneline | wc -l | sed 's/^ *//'`
	popd > /dev/null
}

pushd $repoloc > /dev/null
for i in *
do
	if [[ -f $i ]]
	then
		continue
	fi
	stat-repo $i
	#echo "{\"$repoName\": \"$i\", \"$noOfMerges\": $merges, \"$moreThanTwo\": $threeParents}" 
	echo "$i,$merges,$threeParents"
done
popd > /dev/null
