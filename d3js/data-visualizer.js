var xAxisFunction = function(d) { return d.LOC_A_TO_SOLVED; }
var yAxisFunction = function(d) { return d.LOC_B_TO_SOLVED; }

function goToData(d) {
	project = d.PROJECT;
	commit = d.SHA;
	file = d.FILE;
	window.location.href = "data/" + project + "/" + commit + "/" +file + "/loc/";
}