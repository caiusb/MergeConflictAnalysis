source('common.R')

data <- loadData(resultsFolder)

if(exists("SECONDS_PER_DAY") != TRUE) {
  SECONDS_PER_DAY <- 88400
  lockBinding("SECONDS_PER_DAY", globalenv())
}

calculateTimeDifferences <<- function(data) {
  data <- calculateResolutionTime(data)  
  data <- calculateTimeBetweenTips(data)
  return(data)
}

calculateTimeBetweenTips <- function(data) {
  data$TIME_TIPS <- data$TIME_A - data$TIME_B
  return(data)
}

calculateResolutionTime <- function(data) {
  data$RESOLUTION_TIME <- data$TIME_SOLVED - data$TIME_A
  return(data)
}

calculateEffort <- function(data) {
  weightedConflictSize <- data$LOC_A_TO_SOLVED/data$LOC_SIZE_A + data$LOC_B_TO_SOLVED/data$LOC_SIZE_B
  weightedAverageConflictSize <- (weightedConflictSize)/2
  averageConflictSize <- (data$LOC_A_TO_SOLVED + data$LOC_B_TO_SOLVED)/2
  deviationFromDiagonal <- (data$LOC_B_TO_SOLVED-data$LOC_A_TO_SOLVED)/sqrt(2)
  effort <- deviationFromDiagonal*weightedAverageConflictSize
  return(effort)

}

trimSolveTimeGreaterThanDays <-function(data, days){
  return(data[data$RESOLUTION_TIME <= days*SECONDS_PER_DAY, ]);
}

timedData <- calculateTimeDifferences(data)
timedCommitData <- calculateTimeDifferences(createCommitData(timedData))
trimmedCommitData <- trimSolveTimeGreaterThanDays(timedCommitData, 1)
trimmedCommitData$EFFORT <- calculateEffort(trimmedCommitData)
print(summary(trimmedCommitData$RESOLUTION_TIME))
cat("Standard deviation: ", sd(trimmedCommitData$RESOLUTION_TIME))
hist(trimmedCommitData$RESOLUTION_TIME, main="Resolution time", xlab="Time (s)", breaks=50)
plotWithLinearRegression(trimmedCommitData, "RESOLUTION_TIME", "EFFORT")