#!/bin/bash

source 'common.sh'

repoloc=$(resolve-path $1)
resultloc=$(resolve-path $2)

wd=`pwd`

cd $repoloc
for i in *
do
	if [ ! -d $i ]
	then
		continue
	fi
	cd $i
	python $wd/get-merged-lines.py $PWD > $resultloc/$i.json
	cd ../
done