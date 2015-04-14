#!/bin/bash

source common.sh

if [[ "$#" -ne 2 ]]
then
	echo "Usage: ./run.sh <dir with repos> <dir where to put the results>"
	exit
fi

repoloc=$(resolve-path $1)
resultsloc=$(resolve-path $2 )
results_suffix='.csv'

dir=$PWD
orderfile=$repoloc/'order.txt'

pushd $resultsloc > /dev/null
git pull > /dev/null
popd > /dev/null

pushd $repoloc > /dev/null

while read line
do
    echo "Processing: $line"
    date=`date`
    java -Xmx12G -jar $dir/../MergingConflictAnalysis.jar "$line" > $resultsloc/$line$results_suffix 2>$resultsloc/log/$line.txt

    pushd $resultsloc > /dev/null
    git add $line$results_suffix log/$line.txt > /dev/null
    git commit -m "Results as of $date" > /dev/null
    git push > /dev/null
    popd > /dev/null
done < $orderfile

popd > /dev/null
