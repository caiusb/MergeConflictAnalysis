source('common.R')

resultsFolder <- "../../results/merge-data"

data <- read.csv(concat(resultsFolder, "/../mergedPR.csv"))
data$HAS_PULL_REQUEST <- ifelse(is.na(data$PR), FALSE, TRUE)
#data$HAS_PULL_REQUEST <- factor(data$HAS_PULL_REQUEST)
data$IS_C <- ifelse(data$IS_CONFLICT == TRUE, 1, 0)
#data$IS_C <- factor(data$IS_C)

prcomp(data)

print(summary(mylogit))