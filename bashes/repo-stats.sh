#!/bin/bash

source common.sh

repoloc=$(resolve-path $1)

repoName="Repo name"
noOfMerges="Merges"
moreThanTwo="Merges 3 parents"

function stat-repo() {
	repoPath=$1
	pushd $repoPath > /dev/null
	merges=`git log --merges --oneline | wc -l`
	threeParents=`git log --min-parents=3 --oneline | wc -l`
}

echo $repoloc
pushd $repoloc > /dev/null
for i in *
do
	stat-repo $i
	echo "{\"$repoName\": \"$i\", \"$noOfMerges\": $merges, \"$moreThanTwo\": $threeParents}" 
done
popd
