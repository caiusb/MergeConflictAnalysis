#!/bin/bash

base='/scratch/brindesc'
results='/scratch/brindesc/merges'

mkdir -P $results

function copyFilesAsIs() {
    conflicting=$0
    folderName=$1

    print '%s\n' "$conflicting" | while IFS= read line
    do
        mkdir -p $results/$project/$sha/$folderName
        cp $file $results/$project/$sha/$folderName/$(basename $file)
    done
}

while read line
do
    sha=`echo $line | cut -d "," -f1`
    project=`echo $line | cut -d "," -f2`
    if [ -e $base/'my-repos'/$project ]
    then
        pushd $base/'my-repos'/$project > /dev/null
    elif [ -e $base/'ase16-repos'/$project ]
    then
        pushd $base/'ase16-repos'/$project > /dev/null
    else
        echo "Count not find $project"
        continue
    fi

    git checkout -f .
    parents=`git status --quiet --format="%P"`
    one=`echo $parents | cut -d " " -f1`
    two=`echo $parents | cut -d " " -f2`
    git checkout $one
    git merge $two
    conficting=`git status --procelain | grep "^UU"`
    echo $conflicting | while read line
    do
        mkdir $results/$project/$sha
        file=`echo $line | cut -d " " -f2`
        cp $file $results/$project/$sha/$(basename $file)
    done

    git reset --hard
    git checkout -f $one
    copyFilesAsIs($conflicting, "one")
    git checkout -f $two
    copyFilesAsIs($conflicting, "two")
    git checkout -f $sha
    copyFilesAsIs($conflicting, "solved")
    git checkout -f master
    popd > /dev/null
done
