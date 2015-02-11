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

uname=`uname`
if [[$uname = Linux]]
then
	ramdisk="/mnt/ramdisk/merging/"
else if [[$uname = Darwin]]
	ramdisk="/Volumes/RAM-Disk/"
else
	echo "Unsupported operating system."
	return -1
fi

pushd $repoloc > /dev/null

while read line
do
    tmploc="$ramdisk$line"
    date=`date`
    echo "Processing $line"
    cp -r $line "$tmploc"
    pushd "$tmploc" > /dev/null
    git checkout -f master > /dev/null
    popd > /dev/null
    java -Xmx1G -jar $dir/../MergingConflictAnalysis.jar "$tmploc" > $resultsloc/$line.json 2>$resultsloc/log/$line.txt
    rm -rf $tmploc

    pushd $resultsloc > /dev/null
    git add $line.json log/$line.txt > /dev/null
    git commit -m "Results as of $date" > /dev/null
    git push > /dev/null
    popd < /dev/null
done < $orderfile

popd > /dev/null
