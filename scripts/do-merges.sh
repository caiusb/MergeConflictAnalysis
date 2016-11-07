#!/bin/bash

base='/scratch/brindesc'
results='/scratch/brindesc/merges'

mkdir -p $results

function copyFilesAsIs() {
    conflicting="$1"
    folderName=$2

    mkdir -p $results/$project/$sha/$folderName

    printf '%s\n' "$conflicting" | while IFS= read line
    do
        file=`echo $line | cut -d " " -f2`
        cp "$file" "$results/$project/$sha/$folderName/$(basename $file)"
    done
}

while read line
do
    sha=`echo $line | cut -d "," -f1`
    project=`echo $line | cut -d "," -f3`
    mergeBase=`echo $line | cut -d "," -f2`
    if [ -d $base/'my-repos'/"$project" ]
    then
        pushd $base/'my-repos'/$project > /dev/null
    elif [ -d $base/'ase16-repos'/"$project" ]
    then
        pushd $base/'ase16-repos'/$project > /dev/null
    else
        echo "Could not find $project"
        continue
    fi

    git reset --hard
    git checkout -f .
    parents=`git show --quiet --format="%P" $sha`
    one=`echo $parents | cut -d " " -f1`
    two=`echo $parents | cut -d " " -f2`
    git checkout $one
    git merge $two
    conflicting=`git status --porcelain | grep "^U"`
    copyFilesAsIs "$conflicting"  "merged"

    git reset --hard
    git checkout -f .
    git checkout -f $one
    copyFilesAsIs "$conflicting" "one"
    git checkout -f $two
    copyFilesAsIs "$conflicting" "two"
    git checkout -f $sha
    copyFilesAsIs "$conflicting" "solved"
    git checkout -f $mergeBase
    copyFilesAsIs "$conflicting" "base"
    git checkout -f master
    popd > /dev/null
done
