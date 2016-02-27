#!/bin/bash

source common.sh
source discover.sh

if [[ "$#" -ne 2 ]]
then
	echo "Usage: ./run.sh <dir with repos> <dir where to put the results>"
	exit
fi

export M2_HOME=`mvn --version | grep "Maven home" | rev | cut -d':' -f1 | rev | sed "s/^ *//"`

pushd MergeConflictAnalysis > /dev/null
sbt assembly
mv target/scala-2.11/MergeConflictAnalysis-assembly* ../
popd > /dev/null


repoloc=$(resolve-path $1)
resultsloc=$(resolve-path $2 )
results_suffix='.csv'
javaopts='-Xmx12G'
#vizdataopts=''
vizdataopts="-url-folder mergeviz -viz-folder $resultsloc/../../viz/data"

dir=$PWD
orderfile=$repoloc/'order.txt'

pushd $resultsloc > /dev/null
git pull > /dev/null
popd > /dev/null

pushd $repoloc > /dev/null

function run-for-repo() {
    path=$1

    reponame=$(basename $path)
    echo "Processing: $reponame"
    date=`date`
    java $javaopts -jar $dir/../MergingConflictAnalysis.jar -output $resultsloc/$reponame$results_suffix $vizdataopts $path 2>$resultsloc/log/$reponame.txt

    pushd $resultsloc > /dev/null
    git add $reponame$results_suffix log/$reponame.txt > /dev/null
    git commit -m "Results as of $date" > /dev/null
    git push > /dev/null
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
