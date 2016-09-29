#!/bin/bash

source 'common.sh'

function printBranch() {
    sha=$1
    branch=$2
    commits="$3"
    printf "%s" "$commits" | while read commit
    do
        echo "$sha,$branch,$commit"
    done
}

function getBranchCommits() {
    repo=$1
    resultFolder=$2
    merges=`git log --merges --format="%H"`
    printf "%s" "$merges" | while read merge
    do
        parents=`git show --format="%P" -s $merge`
        firstParent=`echo $parents | cut -d" " -f1`
        secondParent=`echo $parents | cut -d" " -f2`
        firstDate=`git show --format="%at" -s $firstParent`
        secondDate=`git show --format="%at" -s $secondParent`
        if [ $firstDate -gt $secondDate ]
        then
            firstParent=$secondParent
            secondParent=`echo $parents | cut -d" " -f1`
        fi
        base=`git merge-base $firstParent $secondParent`
        aCommits=`git log --format="%H,%at" $base..$firstParent`
        printBranch $merge "A" "$aCommits" > $resultFolder/$repo.csv
        bCommits=`git log --format="%H,%at" $base..$secondParent`
        printBranch $merge "B" "$bCommits" > $resultFolder/$repo.csv
    done
}

process-repos getBranchCommits $1 $2
