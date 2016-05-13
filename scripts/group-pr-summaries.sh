#!/bin/bash

source common.sh

srcfolder=$(resolve-path $1)
dstfolder=$(resolve-path $2)

pushd $srcfolder > /dev/null

for i in *
do
	mv $i/summary.csv $dstfolder/$i'.csv'
done

popd > /dev/null
