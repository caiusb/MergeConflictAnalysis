#!/bin/bash

source common.sh
source discover.sh

if [[ "$#" -ne 2 ]]
then
	echo "Usage: ./run.sh <dir with repos> <dir where to put the results>"
	exit
fi

export M2_HOME=`mvn --version | grep "Maven home" | rev | cut -d':' -f1 | rev | sed "s/^ *//"`

pushd ../MergeConflictAnalysis > /dev/null
sbt assembly
mv target/scala-2.11/MergeConflictAnalysis-assembly-1.0.0.jar ../
popd > /dev/null


repoloc=$(resolve-path $1)
resultsloc=$(resolve-path $2 )
results_suffix='.csv'
javaopts='-Xmx12G'
vizdataopts=''
#vizdataopts="-url-folder mergeviz -viz-folder $resultsloc/../../viz/data"

dir=$PWD
orderfile=$repoloc/'order.txt'

if [ ! -d $repoloc ]
then
    echo "Repository folder does not exist"
    exit 1
fi

if [ ! -d $resultsloc ]
then
    echo "Results folder does not exist"
    exit 1
fi

if [ ! -d $resultsloc/"log" ]
then
    echo "Log folder does not exist. Creating one..."
    mkdir $resultsloc/"log"
fi   

pushd $resultsloc > /dev/null
if [ ! -d ".git" ]
then
    echo "Git repo is not initalized. Initializing..."
    git init
else
    git pull > /dev/null 2>&1
fi
popd > /dev/null

pushd $repoloc > /dev/null

function run-for-repo() {
    path=$1

    reponame=$(basename $path)
    echo "Processing: $reponame"
    date=`date`
    java $javaopts -jar $dir/../MergeConflictAnalysis-assembly-1.0.0.jar -output $resultsloc/$reponame$results_suffix $vizdataopts $path 2>$resultsloc/log/$reponame.txt

    pushd $resultsloc > /dev/null
    git add $reponame$results_suffix log/$reponame.txt > /dev/null
    git commit -m "Results as of $date" > /dev/null
    git push > /dev/null 2>&1
    popd > /dev/null    
}

if [ -e "order.txt" ] 
then
    while read line
    do
        run-for-repo $line
    done < $orderfile
else
    discover $repoloc "run-for-repo"
fi
popd > /dev/null
