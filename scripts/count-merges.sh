#!/bin/bash

header="PROJECT,NO_MERGES"

folder=$1

echo $header

pushd $folder > /dev/null
for i in *
do
	cd $i
	echo $i","`git log --merges --oneline | wc -l`
	cd ../
done

popd > /dev/null
