#!/bin/bash

base='/scratch/brindesc'
results='/scratch/brindesc/merges'

mkdir -p $results

while read line
do
    sha=`echo $line | cut -d "," -f2`
    project=`echo $line | cut -d "," -f3`
    if [ -e $base/'my-repos'/$project ]
    then
        pushd $base/'my-repos'/$project > /dev/null
    elif [ -e $base/'ase16-repos'/$project ]
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
    conflicting=`git status --porcelain | grep "^UU"`
    printf '%s\n' "$conflicting" | while IFS= read line
    do
        mkdir -p $results/$project/$sha
        file=`echo $line | cut -d " " -f2`
        cp $file $results/$project/$sha/$(basename $file)
    done
    popd > /dev/null
done
