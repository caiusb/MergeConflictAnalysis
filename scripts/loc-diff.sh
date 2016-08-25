#!/bin/bash

while read project
do
    if [ -e "/scratch/brindesc/my-repos/$project" ]
    then
        repo="/scratch/brindesc/my-repos/$project"
    elif [ -e "/scratch/brindesc/ase16-repos/$project" ]
    then
        repo="/scratch/brindesc/ase16-repos/$project" 
    else
        echo "Could not find $project"
        continue
    fi
    pushd $repo
    output=`git log --merges --format="%H"`
    printf "%s\n" $output | while read sha
    do
        parents=`git show --quiet --format="%P" $sha`
        result=`git diff --stat $parents | grep -E "java\s" | tr -s ' ' | cut -d" " -f4 | paste -sd+ - | bc`
        if [ -z $result ]
        then
            result="0"
        fi
        echo $project,$sha,$result
    done
    popd
done < $1
