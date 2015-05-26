#!/bin/bash

source common.sh

if [[ "$#" -ne 2 ]]
then
	echo "Usage: ./run.sh <dir with repos> <dir where to put the results>"
	exit
fi

name=`uname`
if [ $name = Linux ]
then
    tmpfolder="/tmp/"
elif [ $name = Darwin ]
then
    tmpfolder="/var/folders/"
else
    echo "Unsupported operating system."
    exit -1
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
    java -Xmx12G -jar $dir/../MergingConflictAnalysis.jar -output $resultsloc/$line$results_suffix -url-folder "mergeviz" -viz-folder "../viz/data" "$line" 2>$resultsloc/log/$line.txt

    pushd $resultsloc > /dev/null
    git add $line$results_suffix log/$line.txt > /dev/null
    git commit -m "Results as of $date" > /dev/null
    git push > /dev/null
    popd > /dev/null

    find $tmpfolder -name '*java' -exec rm {} +
done < $orderfile

popd > /dev/null
