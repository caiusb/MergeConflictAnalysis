#!/bin/bash

if [[ "$#" -ne 2 ]]
then
	echo "Usage: ./run.sh <dir with repos> <dir where to put the results>"
	exit
fi


function resolve-path() {
	if [[ $1 = /* ]]
	then
		echo $1
	else
		echo $PWD/$1
	fi
}

repoloc=$(resolve-path $1)
resultsloc=$(resolve-path $2 )

orderfile='order.txt'

dir=$PWD
pushd $repoloc

while read line
do
	tmploc=/mnt/ramdisk/merging/$line
	date=`date`
    cp -r $line $tmploc
    pushd $tmploc
    git checkout -f master
    popd
    java -Xmx1G -jar $dir/MergingConflictAnalysis.jar $tmploc > $resultsloc/$line.json
    rm -rf $tmploc

	pushd $resultsloc
	git add $line.json
	git commit -m "Results as of $date"
	git push
	popd
done < $orderfile

popd
