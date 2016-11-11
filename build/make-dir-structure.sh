#!/bin/bash

if [ ! -d "scripts" ]
then
	mkdir "scripts"
fi

if [ ! -d "../../build-data" ] 
then
	mkdir "../../build-data"
fi

if [ ! -d "../../build-data/output" ]
then
	mkdir "../../build-data/output"
fi

#if [ ! -d "../../build-data/errors" ]
#then
#	mkdir "../../build-data/errors"
#fi

if [ ! -d "../../build-data/results" ]
then
	mkdir "../../build-data/results"
fi
