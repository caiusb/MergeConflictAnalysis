#!/bin/bash

filesFolder=$1

pushd $filesFolder > /dev/null

hasHeader=false

for i in *.csv
do
    project=`echo $i | cut -d'.' -f1`
    header=`head -n 1 $i`
    if [ $hasHeader != true ]
    then
        echo "$header,PROJECT"
        hasHeader=true
    fi

    while read line
    do
        if [ "$line" == "$header" ]
        then
            continue
        else
            echo $line,$project
        fi
    done < $i
done

popd > /dev/null
