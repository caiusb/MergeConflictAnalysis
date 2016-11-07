#!/bin/bash

source common.sh

folder=$(resolve-path $1)
results=$(resolve-path $2)

function getAuthorInfo() {
    repo=$1
    results=$2

    project=$(basename $repo)
    resultfile=$results/$project
    echo "SHA,AUTHOR,COMMITTER" > $resultfile.csv
    git log --format="%H,\"%an\",\"%aE\"" >> $resultfile.csv
}

process-repos getAuthorInfo $folder $results
