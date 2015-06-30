#!/bin/bash

source common.sh
source discover.sh

if [[ "$#" -ne 2 ]]
then
	echo "Usage: ./run.sh <dir with repos> <dir where to put the results>"
	exit
fi

repoloc=$(resolve-path $1)
resultsloc=$(resolve-path $2 )
results_suffix='.csv'
javaopts='-Xmx12G'
vizdataopts='-url-folder "mergeviz" -viz-folder "../viz/data" "$repo"'

dir=$PWD
orderfile=$repoloc/'order.txt'

pushd $resultsloc > /dev/null
git pull > /dev/null
popd > /dev/null

pushd $repoloc > /dev/null

function run-for-repo() {
    repo=$1

    echo "Processing: $repo"
    date=`date`
    java $javaopts -jar $dir/../MergingConflictAnalysis.jar -output $resultsloc/$repo$results_suffix $vizdataopts 2>$resultsloc/log/$repo.txt

    pushd $resultsloc > /dev/null
    git add $repo$results_suffix log/$repo.txt > /dev/null
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
