#!/bin/bash

if [ ! -d "scripts" ]
then
	mkdir "scripts"
fi

if [ ! -d "../../build-merge-data" ] 
then
	mkdir "../../build-merge-data"
fi

if [ ! -d "../../build-merge-data/output" ]
then
	mkdir "../../build-merge-data/output"
fi

#if [ ! -d "../../build-data/errors" ]
#then
#	mkdir "../../build-data/errors"
#fi

if [ ! -d "../../build-merge-data/results" ]
then
	mkdir "../../build-merge-data/results"
fi
