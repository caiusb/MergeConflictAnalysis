#!/bin/bash

source common.sh

if [[ "$#" -ne 2 ]]
then
	echo "Usage: ./run.sh <dir with repos> <dir where to put the results>"
	exit
fi

repoloc=$(resolve-path $1)
resultsloc=$(resolve-path $2 )

orderfile=$repoloc/'order.txt'

dir=$PWD
pushd $repoloc > /dev/null

while read line
do
	tmploc=/mnt/ramdisk/merging/$line
	date=`date`
	echo "Processing $line"
    cp -r $line $tmploc
    pushd $tmploc > /dev/null
    git checkout -f master > /dev/null
    popd > /dev/null
    java -Xmx1G -jar $dir/../MergingConflictAnalysis.jar $tmploc > $resultsloc/$line.json 2>$resultsloc/log/$line.txt
    rm -rf $tmploc

	pushd $resultsloc > /dev/null
	git add $line.json log/$line.txt
	git commit -m "Results as of $date"
	git push
	popd < /dev/null
done < $orderfile

popd
