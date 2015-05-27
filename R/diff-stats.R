source("common.R")
source("distance-metrics.R")
source("time-metrics.R")

data <- loadData(resultsFolder)

#Trimming the cases where I couldn't find a diff, for various reasons...
data <- data[data$LOC_A_TO_SOLVED >= 0, ]
data <- data[data$LOC_B_TO_SOLVED >= 0, ]
data <- data[data$LOC_A_TO_B >= 0, ]

#plotDistanceMetrics(data)
plotTimeMetrics(data)
#plotWithLinearRegression(data, "AST_A_TO_B", "LOC_A_TO_B");