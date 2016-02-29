#!/bin/bash

touch jobs.txt

cd "scripts"

for i in *
do
	basename=`echo $i | rev | cut -d"." -f2 | rev`
	qsub -cwd -S /bin/bash -o "../../../build-data/output/$basename" -e "../../../build-data/errors/$basename" $i >> ../jobs.txt
done