function scatterPlot() {

	var height = 500;
	var width = 900;
	var legendWidth = 200;

	var margin = {top: 20, left: 30, right: 30, bottom: 20};

	var circleRadius = 3.5;

	var getInnerWidth = function() {
		return width - margin.left - margin.right;
	}

	var getInnerHeight = function() {
		return height - margin.top - margin.bottom;
	}

	var xValue = function(d) { return +d.xValue; } // force to number
	var yValue = function(d) { return +d.yValue; } // force to number
	var label = function(d) { return d.label; } 
	var category = function(d) { return d.category; }
	var clickAction = function(d) {}
	var dataKey = function(d) { return d; }

	var chart = function(selection) {
		selection.each(function(data) {

			var initialData = data;

			var xScale = d3.scale.linear()
				.rangeRound([margin.left, getInnerWidth() - legendWidth])
				.domain([d3.min(data, xValue) - 1, d3.max(data,xValue) + 1]);
			var xAxis = d3.svg.axis()
				.scale(xScale)
				.orient("bottom");

			var yScale = d3.scale.linear()
				.rangeRound([getInnerHeight(), margin.top])
				.domain([d3.min(data, yValue) - 1, d3.max(data, yValue) + 1]);
			var yAxis = d3.svg.axis()
				.scale(yScale)
				.orient("left");

			var zoom = function() {
				svg.select("g.x.axis").call(xAxis);
				svg.select("g.y.axis").call(yAxis);
				circle = svg.selectAll("circle");
				circle.attr("transform", circleTransform);
			}

			var svg = d3.select(this).append("svg")
				.attr("height", height)
				.attr("width", width)
				.append("g")
				.attr("height", getInnerHeight())
				.attr("width", getInnerWidth())
				.call(d3.behavior.zoom().x(xScale).y(yScale).scaleExtent([1, 8]).on("zoom", zoom));

			var clip = svg.append("clipPath")
				.attr("id","clip")
				.append("rect")
				.attr("x", margin.left)
				.attr("y", margin.top)
				.attr("width", getInnerWidth() - legendWidth)
				.attr("height", getInnerHeight() - margin.top);

			svg.append("rect")
    			.attr("class", "overlay")
    			.attr("x", margin.left)
    			.attr("y", margin.top)
    			.attr("width", getInnerWidth() - legendWidth)
    			.attr("height", getInnerHeight() - margin.top);

			var circleTransform = function(d) {
				return "translate(" + xScale(xValue(d)) + "," + yScale(yValue(d)) + ")";
			};

			var color = d3.scale.category20();
			color.domain(data.map(function(d) { return category(d); }));
			var isVisible = {};
			color.domain().forEach(function (name) {
				isVisible[name] = true;
			});

			var tooltip = d3.select("body").append("div")
				.attr("class", "tooltip")
				.style("opacity", 0);

				var configureCircle = function(circle) {
					circle.style("fill", function(d) { return color(category(d)); })
						.style("fill-opacity",1e-6)
						.attr("transform", circleTransform)
						.attr("r", circleRadius)
						.on("mouseover", function(d) {
							tooltip.transition()
								.duration(200)
								.style("opacity",1);
							tooltip.html(label(d) + "<br />(" +
									xValue(d) + ", " + yValue(d) + ")")
								.style("left", (d3.event.pageX + 5) + "px")
								.style("top", (d3.event.pageY - 28) + "px");
						})
						.on("mouseout", function(d) {
							tooltip.transition()
								.duration(200)
								.style("opacity",0);
						})
						.on("click", clickAction)
						.transition()
						.style("fill-opacity", 1);
				}

			var circle = svg
				.append("g")
				.attr("clip-path", "url(#clip)")
				.selectAll(".dot")	
				.data(data, dataKey)
				.enter()
				.append("circle");
			configureCircle(circle);

			svg.append("g")
				.attr("class","x axis")
				.attr("transform","translate(0," + getInnerHeight() + ")")
				.call(xAxis);
			
			svg.append("g")
				.attr("class","y axis")
				.attr("transform","translate(" + margin.left + ",0)")
				.call(yAxis);

			var legendColorFunction = function(d) { 
				return isVisible[d] ? color(d) : "#F1F2F2"
			};
			var legend = svg.selectAll(".legend")
				.data(color.domain())
				.enter()
				.append("g")
				.attr("class", "legend")
				.attr("transform", function(d,i) { return "translate(0," + i *20 + ")"; })
				.attr("height", getInnerHeight());
			legend.append("rect")
				.attr("x",width - 18)
				.attr("width",18)
				.attr("height",18)
				.style("fill", legendColorFunction)
				.on("click", function(d) {
					isVisible[d] = !isVisible[d];
					d3.select(this).transition().duration(500)
						.style("fill", legendColorFunction);

					var newData = initialData
						.filter(function (d) {
							return isVisible[d.PROJECT];
						});
					newCircle = svg.selectAll("circle").data(newData, dataKey);
					newCircle.transition().duration(2000);
					newCircle.exit().transition().style("fill-opacity", 1e-6).remove();
					configureCircle(newCircle.enter().append("circle"));
				});


			legend.append("text")
				.attr("x", width-24)
				.attr("y", 9)
				.attr("dy", ".35em")
				.style("text-anchor","end")
				.text(function(d) { return d; });
		});
	}

	chart.height = function(newHeight) {
		if (arguments.length == 0)
			return height;
		height = newHeight;
		return chart;
	}

	chart.width = function(newWidth) {
		if (arguments.length == 0)
			return width;
		width = newWidth;
		return chart;
	}

	chart.circleRadius = function(newRadius) {
		if (arguments.length == 0)
			return circleRadius;
		circleRadius = newRadius;
		return chart;
	}

	chart.xValue = function(newXValueFunction) {
		if (arguments.length == 0)
			return xValue;
		xValue = newXValueFunction;
		return chart;
	}

	chart.yValue = function(newYValueFunction) {
		if (arguments.length == 0)
			return yValue;
		yValue = newYValueFunction;
		return chart;
	}

	chart.label = function(newLabelFunction) {
		if (arguments.length == 0)
			return label;
		label = newLabelFunction;
		return chart;
	}

	chart.category = function(newCategoryFunction) {
		if (arguments.length == 0)
			return category;
		category = newCategoryFunction;
		return chart;
	}

	chart.clickAction = function(newClickActionFunction) {
		if (arguments.length == 0)
			return clickAction;
		clickAction = newClickActionFunction;
		return chart;
	}

	chart.dataKey = function(newDataKeyFunction) {
		if (arguments.length == 0)
			return dataKey;
		dataKey = newDataKeyFunction;
		return chart;
	}

	return chart;
}
