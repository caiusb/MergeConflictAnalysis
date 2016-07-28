#!/bin/bash

cd /scratch/brindesc/ase-repos

ls | while read project
do
    cd $project
    git log --merges --format="%H,%P" | while read line
    do 
        sha=`echo $line | cut -d"," -f1`
        p1=`echo $line | cut -d"," -f2`
        p2=`echo $line | cut -d"," -f3`
        base=`git merge-base $p1 $p2`
        onebranch=`git log $base..$p1 --format="%an" | sort | uniq | wc -l`
        twobranch=`git log $base..$p1 --format="%an" | sort | uniq | wc -l`
        echo $project,$sha,$onebranch,$twobranch
    done
done
