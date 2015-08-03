#!/bin/bash

source common.sh

folder=$(resolve-path $1)
results=$(resolve-path $2)

function getRegularCommits() {
	repo=$1
	results=$2

	resultfile=$results/$(basename $repo)
	echo "SHA,COMMIT_TIME,AUTHOR" > $resultfile.csv
	git log --max-parents=1 --format="%H,%at,%ae" >> $resultfile.csv
}

process-repos getRegularCommits $folder $results
