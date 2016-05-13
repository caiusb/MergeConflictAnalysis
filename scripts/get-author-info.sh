#!/bin/bash

source common.sh

folder=$(resolve-path $1)
results=$(resolve-path $2)

function getAuthorInfo() {
	repo=$1
	results=$2

	resultfile=$results/$(basename $repo)
	echo "SHA,AUTHOR,COMMITTER" > $resultfile.csv
	git log --format="%H,%aE,%cE" >> $resultfile.csv
}

process-repos getAuthorInfo $folder $results
