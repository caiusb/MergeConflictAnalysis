#!/bin/bash

repo=$1

cd $repo
repoName=$(basename $repo)
authors=`git log --format="%H,%an"`
printf '%s\n' "$authors" | while IFS= read -r line
do
    echo "$repoName,$line"
done
cd ../
