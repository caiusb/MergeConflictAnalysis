#!/bin/bash

source common.sh

folder=$(resolve-path $1)
results=$(resolve-path $2)
orderfile=$folder/'order.txt'

pushd $folder > /dev/null

for i in *
do
	if [ -f $i ]
	then
		continue
	fi
	pushd $i > /dev/null
	if [ -e '.git' ]
	then
		resultfile=$results/$(basename $i)
		echo "SHA,COMMIT_TIME,AUTHOR" > $resultfile.csv
		git log --max-parents=1 --format="%H,%at,%ae" >> $resultfile.csv
	fi
	popd > /dev/null
done

popd > /dev/null