#!/bin/bash

filesFolder=$1

pushd $filesFolder

hasHeader=false

for i in *.csv
do
    project =`echo $i | cut -d'.' -f1`
    header=`head -n 1 $i`
    if [ !$hasHeader ]
    then
        echo "$header,project"
        hasHeader=true
    fi

    while read line
    do
        if [ $line -eq $header ]
        then
            continue
        else
            echo $line,$project
        fi
    done < $i
done

popd
