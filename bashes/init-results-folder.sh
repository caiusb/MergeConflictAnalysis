#!/bin/bash

source common.sh

if [[ "$#" -ne 1 ]]
then
	echo "Usage: ./init-results-folder.sh <results folder>"
	exit
fi

resultsloc=$(resolve-path $1)

mkdir $resultsloc

pushd $resultsloc > /dev/null

mkdir log
git init

popd $resultsloc > /dev/null