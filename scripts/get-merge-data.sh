#!/bin/bash

sampleFile=$1
#repoDir1='/scratch/brindesc/ase16-repos'
repoDir='/scratch/brindesc/icse17-corpus'
results='/scratch/brindesc/200-sample-merges'

function getChanges {
	project=$1
	sha=$2
	base=$3
	one=$4
	two=$5

	mergeBase="$results/$(basename $project)/$sha"
	mkdir -p "$mergeBase"
	mkdir -p "$mergeBase/one"
	mkdir -p "$mergeBase/two"
	mkdir -p "$mergeBase/base"
	mkdir -p "$mergeBase/solved"
        mkdir -p "$mergeBase/merged"
	pushd $project

	git reset --hard
	git checkout -f $one
	git merge $two
	s=`git status --porcelain | grep "^UU"`
	declare -a conflictingFiles
	echo "$s" | while read line
	do
		file=`echo $line | cut -d' ' -f2`
		conflictingFiles=("${conflictingFiles[@]}" "$file")
	done

	for f in "${conflictingFiles[@]}"
	do
                filename=$(basename $f)
		cp $f $mergeBase/merged/$filename
	done

        git reset --hard
        git clean -f -d
	for f in "${conflictingFiles[@]}"
	do
                filename=$(basename $f)
                echo $filename
		git show $base:$f > $mergeBase/base/$filename
		git show $one:$f > $mergeBase/one/$filename
		git show $two$f > $mergeBase/two/$filename
		git show $sha:$f > $mergeBase/solved/$filename
        done

	popd
}

while read line
do
	project=`echo $line | cut -d',' -f5`
	baseSHA=`echo $line | cut -d',' -f2`
	p1SHA=`echo $line | cut -d',' -f4`
	p2SHA=`echo $line | cut -d',' -f3`
	sha=`echo $line | cut -d',' -f1`
        if [ ! -d $repoDir/$project ]
        then
                continue
        else
                getChanges "$repoDir/$project" $sha $baseSHA $p1SHA $p2SHA
        fi
done < $sampleFile
