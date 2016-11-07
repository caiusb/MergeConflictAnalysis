#!/bin/bash

pushd /scratch/brindesc/icse17-corpus > /dev/null

echo "SHA,AUTHOR,PROJECT"
ls | while read project
do
    cd $project
    git log --format="%H,\"%aN\"" | while read commit
    do
        echo $commit,$project
    done
    cd ..
done

popd > /dev/null
