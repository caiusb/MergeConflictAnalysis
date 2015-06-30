#!/bin/bash

function discover() {
	path=$1
	command=$2

	if [[ -d "$path/.git" ]]
	then
		$command $path
	else
		for i in $path/*
		do
			discover $i $command
		done
	fi
}