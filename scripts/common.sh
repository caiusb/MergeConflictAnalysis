function resolve-path() {
	if [[ $1 = /* ]]
	then
		echo $1
	else
		echo $PWD/$1
	fi
}

function process-repos() {
	processFunction=$1
	folder=$(resolve-path $2)
	results=$(resolve-path $3)


	pushd $folder > /dev/null

	for i in *
	do
		if [ -f $i ]
		then
			continue
		fi
		pushd $i > /dev/null
		if [ -e '.git' ]
		then
			echo "$i $results"
		fi
		popd > /dev/null
	done | xargs -n -1 -P 8 -I % $processFunction %

	popd > /dev/null
}
