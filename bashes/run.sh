#!/bin/bash

source common.sh

if [[ "$#" -ne 2 ]]
then
	echo "Usage: ./run.sh <dir with repos> <dir where to put the results>"
	exit
fi

repoloc=$(resolve-path $1)
resultsloc=$(resolve-path $2 )

dir=$PWD
orderfile=$repoloc/'order.txt'

pushd $resultsloc > /dev/null
git pull
popd > /dev/null

pushd $repoloc > /dev/null

while read line
do
    echo "Processing: $line"
    date=`date`
    java -Xmx4G -jar $dir/../MergingConflictAnalysis.jar "$line" > $resultsloc/$line.json 2>$resultsloc/log/$line.txt

    pushd $resultsloc > /dev/null
    git add $line.json log/$line.txt > /dev/null
    git commit -m "Results as of $date" > /dev/null
    git push > /dev/null
    popd > /dev/null
done < $orderfile

popd > /dev/null
