#!/bin/bash

touch jobs.txt

cd "scripts"

for i in *
do
	basename=`echo $i | rev | cut -d"." -f2 | rev`
	qsub -cwd -l a=lx-amd64 -l mem_free=32G -S /bin/bash -o "../../../build-merge-data/output/$basename" -e "../../../build-merge-data/output/$basename" $i >> ../jobs.txt
done
