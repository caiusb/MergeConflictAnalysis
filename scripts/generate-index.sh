#!/bin/bash

#mergesRoot="../../data/1000-merges"
#mergesRoot="/Users/caius/osu/TheMergingProblem/data/test"
#mergesRoot="$HOME/public_html/merges"
mergesRoot="/scratch/brindesc/600-sample-merges/"
cmitMsg="/scratch/brindesc/commit-messages/"

pushd $mergesRoot

htmlHead="<!DOCTYPE html>
<html>

<head>
	<meta charset=\"utf-8\" /> 
	<title>Merge Visualizer</title>
	<link rel=\"stylesheet\" href=\"//web.engr.oregonstate.edu/~brindesc/merges/jsdifflib/diffview.css\"/>
	<link rel=\"stylesheet\" href=\"//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.4.0/styles/default.min.css\">
	<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" crossorigin=\"anonymous\">
</head>

<body>
<div class=\"container\">
"

table="
<div id=\"table\"></div>
<table class=\"table table-hover\">
<tr>
<th>File Name</th>
<th>Merge Result</th>
<th>Base - A</th>
<th>Base - B</th>
<th>Solved - A</th>
<th>Solved - B</th>
<th>A - B</th>
</tr>
"

htmlFoot="
</table>
<div id=\"diffoutput\"></div>
<script src=\"https://code.jquery.com/jquery-1.11.2.min.js\"></script>
<script src=\"//web.engr.oregonstate.edu/~brindesc/merges/jsdifflib/difflib.js\"></script>
<script src=\"//web.engr.oregonstate.edu/~brindesc/merges/jsdifflib/diffview.js\"></script>
<script src=\"//web.engr.oregonstate.edu/~brindesc/merges/diffLOC.js\"></script>
<script src=\"//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.4.0/highlight.min.js\"></script>
<script>hljs.initHighlightingOnLoad();</script>
<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\" integrity=\"sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS\" crossorigin=\"anonymous\"></script>
</div>
</body></html>"

for p in *
do
	if [ ! -d $p ]
	then
		continue
	fi

        echo $p
	cd $p # in project folder
	for c in *
	do
		if [ ! -d $c ]
		then
			continue
		fi

		cd $c # in commit folder
		echo $htmlHead > index.html

		if [ ! -d merged ]
		then
			continue
		fi

                cmitFile=$cmitMsg/$p/$c.txt
                if [ -e $cmitFile ]
                then
                        end=""
                        while read line
                        do
                                if [[ $line == \$\$\$* ]]
                                then
                                        echo $end >> index.html
                                        echo "<div class=\"panel panel-default\" >" >> index.html
                                        echo " <div class=\"panel-heading\">" >> index.html
                                        echo $line | sed -e "s/^\$\$\$\([a-f0-9]\{40\}\)@.*$/\1/g" >> index.html
                                        echo " </div>" >> index.html
                                        echo " <div class=\"panel-body\">" >> index.html
                                        echo "$line<br />" | sed -e "s/^\$\$\$[a-f0-9]\{40\}@\(.*\)$/\1/g" >> index.html
                                        end="</div></div>"
                                else
                                        echo "$line<br />" >> index.html

                                fi
                        done < $cmitFile
                        echo $end >> index.html
                fi

                echo $table >> index.html
		cd merged # in merged folder
		for f in *
		do
			echo "<tr>" >> ../index.html
			echo -n "<td>$f</td>">> ../index.html
			echo -n "<td><button onclick=\"showFile('merged/$f')\">View</button></td>" >> ../index.html
			echo -n "<td><button onclick=\"diffLOC('base/$f', 'one/$f')\">View</button></td>" >> ../index.html
			echo -n "<td><button onclick=\"diffLOC('base/$f', 'two/$f')\">View</button></td>" >> ../index.html
			echo -n "<td><button onclick=\"diffLOC('solved/$f', 'one/$f')\">View</button></td>" >> ../index.html
			echo -n "<td><button onclick=\"diffLOC('base/$f', 'two/$f')\">View</button></td>" >> ../index.html
			echo -n "<td><button onclick=\"diffLOC('one/$f', 'two/$f')\">View</button></td>" >> ../index.html
			echo "</tr>" >> ../index.html
		done
		echo $htmlFoot >> ../index.html
		cd ../../ # back in project folder
	done
	cd ../ # back to root
done

popd
