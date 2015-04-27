function goToData(d) {
	project = d.PROJECT;
	commit = d.SHA;
	file = d.FILE;
	window.location.href = "data/" + project + "/" + commit + "/" +file + "/loc/";
}

