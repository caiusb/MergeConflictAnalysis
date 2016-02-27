#!/bin/bash

cd MergeConflictAnalysis
M2_HOME='mvn --version | grep "Maven home" | rev | cut -d':' -f1 | rev | sed "s/^ *//"'
sbt clean compile test
