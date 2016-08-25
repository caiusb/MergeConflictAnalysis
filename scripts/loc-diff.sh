#!/bin/bash

for i in $1/*
do
    if [ ! -d $i ]
    then
        continue
    fi
    project=$i
    cd $i
    output=`git log --merges --format="%H"`
    printf "%s\n" $output | while read sha
    do
        parents=`git log --format="%P" $sha`
        result=`git diff --stat $parents | grep -E "java\s" | tr -s ' ' | cut -d" " -f4 | paste -sd+ - | bc`
        if [ -z $result ]
        then
            result="0"
        fi
        echo $project,$sha,$result
    done
    cd ../
done
