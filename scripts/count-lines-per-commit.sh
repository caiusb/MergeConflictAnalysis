#!/bin/bash

source 'common.sh'

MOD_PATTERN='^.+(\[-|\{\+).*$'
ADD_PATTERN='^\{\+.*\+\}$'
REM_PATTERN='^\[-.*-\]$'

function process() {
    echo "SHA,LOC" > $2/$1.csv
    git log --format="%H" | while read sha
    do
        change=`git show $sha --word-diff --unified=0 | sed -nr \
            -e "s/$MOD_PATTERN//p" \
            -e "s/$ADD_PATTERN//p" \
            -e "s/$REM_PATTERN//p" \
            | sort | uniq -c | awk '{s+=$1} END {print s}' |  sed -e "s/^$/0/g"`
        echo $sha,$change >> $2/$1.csv
    done
}

process-repos process $1 $2
