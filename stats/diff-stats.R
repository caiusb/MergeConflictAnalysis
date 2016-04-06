source("common.R")
source("distance-metrics.R")
source("time-metrics.R")

resultsFolder <- "../../results/merge-data"
prFolder <- "../../results/pr-summary"
commitFolder <- "../../results/per-commit/"

data <- loadData(resultsFolder)

#data <- data[data$AST_A_TO_SOLVED < 1000, ]
#data <- data[data$AST_B_TO_SOLVED < 1000, ]

plotDistanceMetrics(data)
#plotTimeMetrics(data)
#plotWithLinearRegression(data, "AST_A_TO_B", "LOC_A_TO_B");