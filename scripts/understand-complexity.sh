#!/bin/bash

#Note: This must be run on Babylon 4.

projects='/scratch/ahmed/scripts/list.txt'
outputFiles='/scratch/ahmed/understandoutput'

cat $projects | while read line
do
    project=`echo $line | cut -d"." -f1`
    output=$outputFiles/$project/output.csv
    fromFiles=`cat $output | tail -n +2 | cut -d"," -f1 | sort | uniq`
    toFiles=`cat $output | tail -n +2 | cut -d"," -f2 | sort | uniq`
    echo "File,Out,In" > ../../complexity/$project.csv
    printf "$fromFiles\n$toFiles" | sort | uniq  | while read file 
    do
        if [ -z $file ]
        then
            continue
        fi

        outdeps=`cat $output | tail -n +2 |  grep -e "^$file" | cut -d"," -f2 | sort | uniq | wc -l`
        indeps=`cat $output | tail -n +2 | grep -e ",$file" | cut -d"," -f1 | sort | uniq | wc -l`
        repoFile=`python -c "import os.path; print os.path.relpath('$file', '/scratch/ahmed/repos/$project')"`
        echo $repoFile,$outdeps,$indeps,$project >> ../../complexity/$project.csv
    done
done 
