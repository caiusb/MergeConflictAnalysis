#!/bin/bash

function discover() {
	path=$1
	command=$2

	if [[ -d "$path/.git" ]]
	then
		reponame=$(basename $path)
		$command $reponame
	else
		for i in $path/*
		do
			discover $i $command
		done
	fi
}