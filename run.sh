#!/bin/bash

if [[ "$#" -ne 0 ]]
then
	echo "Usage: ./run.sh <dir with repos> <dir where to put the results>"
fi

if [[ $1 = /* ]]
then
    repoloc=$1
else
    repoloc=$PWD/$1
fi

if [[ $2 = /* ]]
then
    resultsloc=$2
else
    resultsloc=$PWD/$2
fi

dir=$PWD
pushd $repoloc
for i in *
do
    echo "Processing $i"
    tmploc=/mnt/ramdisk/merging/$i
    cp -r $i $tmploc
    pushd $tmploc
    git checkout -f master
    popd
    java -Xmx1G -jar $dir/MergingConflictAnalysis.jar $tmploc > $resultsloc/$i.json
    rm -rf $tmploc
done

popd
