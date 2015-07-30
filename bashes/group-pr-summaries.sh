#!/bin/bash

source common.sh

srcfolder=$(resolve-path $1)
dstfolder=$(resolve-path $2)

pushd $dstfolder > /dev/null
	dstabs=`pwd`
popd > /dev/null

pushd $srcfolder > /dev/null

for i in *
do
	mv $i/summary.csv $dstabs/$i'.csv'
done

popd > /dev/null
