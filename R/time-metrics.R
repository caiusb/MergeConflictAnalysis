source('common.R')

data <- loadData(resultsFolder)

plotTimeMetrics <<- function(data) {
  data$RESOLUTION_TIME <- data$TIME_SOLVED - data$TIME_B
  data$EFFORT <- calculateEffort(data)
  print(data$EFFORT)
  plotWithLinearRegression(data, 'EFFORT','RESOLUTION_TIME')
  return(data)
}

calculateEffort <- function(data) {
  averageConflictSize <- (data$LOC_SIZE_A + data$LOC_SIZE_B)/2
  deviationFromDiagonal <- (data$LOC_A_TO_SOLVED+data$LOC_B_TO_SOLVED)/sqrt((data$LOC_A_TO_SOLVED^2) + (data$LOC_B_TO_SOLVED)^2)
  effort <- deviationFromDiagonal+averageConflictSize
  return(effort)
}

timeBetweenTips <- function(data) {
  return(data$TIME_A - data$TIME_B)
}

data$TIME_TIPS <- timeBetweenTips(data)
trimmed <- data[data$TIME_TIPS <= 5*86400, ]
print(summary(trimmed$TIME_TIPS))
cat("Standard deviation: ", sd(trimmed$TIME_TIPS))
timeForCommit <- aggregate(trimmed$TIME_TIPS, list(time=trimmed$SHA), mean, simplify=TRUE)
hist(timeForCommit$x, main="Resolution time", xlab="Time (s)", breaks=50)

perCommitDf <- createCommitData(trimmed)

plotTimeMetrics(perCommitDf)
#plotWithLinearRegression(trimmed, "TIME_TIPS", "LOC_A_TO_B")