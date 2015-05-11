var xAxisFunction = function(d) { return d.LOC_A_TO_SOLVED; }
var yAxisFunction = function(d) { return d.LOC_B_TO_SOLVED; }

var columnsToTabulate = ["PROJECT", "SHA", "FILE", "AST_A_TO_SOLVED", "AST_B_TO_SOLVED"];

function dataKey (d) { 
	return d.SHA + d.PROJECT + d.FILE; 
}

function createTableHead(columns) {
	var table = d3.select("body").append("table");
	head = table.append("thead");
	table.append("tbody");

	head.append("tr")
		.selectAll("th")
		.data(columns)
		.enter()
		.append("th")
		.text(function(column) { return column; });
}

function tabulate(data, columns) {

	dataRows = d3.select("tbody").selectAll("tr").data(points, dataKey);
	dataRows.exit().remove();
	rows = dataRows.enter().append("tr");
	rows.selectAll("td")
		.data(function (row) {
			return columns.map(function (column) {
				return {column: column, value: row[column]};
			});
		})
		.enter()
		.append("td")
		.html(function (d) { return  d.value; });
}

function goToData(d) {
	project = d.PROJECT;
	commit = d.SHA;
	file = d.FILE;
	//window.location.href = "data/" + project + "/" + commit + "/" +file + "/loc/";

	points = d3.selectAll("circle").data()
		.filter(function (data) {
			return (xAxisFunction(data) == xAxisFunction(d) && yAxisFunction(data) == yAxisFunction(d));
		});

	tabulate(points, columnsToTabulate);
}

function drawPlot() {
	var myScatterPlot = scatterPlot()
		.height(700)
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
	    	.label(function (d) { return d.PROJECT + "\n" + d.SHA + "\n" + d.FILE; })
	    	.category(function (d) { return d.PROJECT; })
	    	.dataKey(dataKey)
			.clickAction(goToData);

		myScatterPlot.legends=[];

		d3.select("#scatterplot").datum(data).call(myScatterPlot);

		createTableHead(columnsToTabulate);
	});
}