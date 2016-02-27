#!/bin/bash

if [ $# -eq 1 ]
then
	path=$1
else
	path='.'
fi

pushd $path > /dev/null
for i in *
do
	echo $i
	if [ -d "$i/.git" ]
	then
		cd $i
		git pull
		cd ../
	fi
done
popd > /dev/null