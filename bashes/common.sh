function resolve-path() {
	if [[ $1 = /* ]]
	then
		echo $1
	else
		echo $PWD/$1
	fi
}
