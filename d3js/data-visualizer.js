var xAxisFunction = function(d) { return d.LOC_A_TO_SOLVED; }
var yAxisFunction = function(d) { return d.LOC_B_TO_SOLVED; }

function goToData(d) {
	project = d.PROJECT;
	commit = d.SHA;
	file = d.FILE;
	window.location.href = "data/" + project + "/" + commit + "/" +file + "/loc/";
}

function drawPlot() {
	var myScatterPlot = scatterPlot()
		.height(600)
		.width(parseInt(d3.select("body").style("width")))
		.circleRadius(4);

	d3.csv("all.csv", function(error, data) {

		//convert strings into numbers
		data.forEach(function(d) {
			d.LOC_A_TO_B = +d.LOC_A_TO_B;
			d.LOC_A_TO_SOLVED = +d.LOC_A_TO_SOLVED;
			d.LOC_B_TO_SOLVED = +d.LOC_B_TO_SOLVED;

			d.AST_A_TO_B = +d.AST_A_TO_B;
			d.AST_A_TO_SOLVED = +d.AST_A_TO_SOVLED;
			d.AST_B_TO_SOLVED = +d.AST_B_TO_SOLVED;
		});

	  	//data to be shown
	  	myScatterPlot.xValue(xAxisFunction)
	    	.yValue(yAxisFunction)
	    	.label(function(d) { return d.PROJECT + "\n" + d.SHA + "\n" + d.FILE; })
	    	.category(function(d) { return d.PROJECT; })
			.clickAction(goToData);

		myScatterPlot.legends=[];

		d3.select("#scatterplot").datum(data).call(myScatterPlot);
	});
}