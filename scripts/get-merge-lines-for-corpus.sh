#!/bin/bash

source 'common.sh'

repoloc=$(resolve-path $1)
resultloc=$(resolve-path $2)

cd $repoloc
for i in *
then
	if [ ! -d $i ]
		continue
	fi
	cd $i
	python get-merged-lines.py > $resultloc/$i.json
	cd ../
done