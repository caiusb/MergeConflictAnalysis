function resolve-path() {
	if [[ $1 = /* ]]
	then
		echo $1
	else
		echo $PWD/$1
	fi
}

function process-repos() {
	echo $2
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
			$processFunction $i $results
		fi
		popd > /dev/null
	done

	popd > /dev/null
}
