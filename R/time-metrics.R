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

perCommitData <- function(data) {
  timeA <- aggregate(TIME_A ~ SHA, data=data, FUN=mean)
  timeB <- aggregate(TIME_B ~ SHA, data=data, FUN=mean)
  timeS <- aggregate(TIME_SOLVED ~ SHA, data=data, FUN=mean)
  locAB <- aggregate(LOC_A_TO_B ~ SHA, data=data, FUN=sum)
  locAS <- aggregate(LOC_A_TO_SOLVED ~ SHA, data=data, FUN=sum)
  locBS <- aggregate(LOC_B_TO_SOLVED ~ SHA, data=data, FUN=sum)
  astAB <- aggregate(AST_A_TO_B ~ SHA, data=data, FUN=sum)
  astAS <- aggregate(AST_A_TO_SOLVED ~ SHA, data=data, FUN=sum)
  astBS <- aggregate(AST_B_TO_SOLVED ~ SHA, data=data, FUN=sum)
  locSizeA <- aggregate(LOC_SIZE_A ~ SHA, data=data, FUN=sum)
  locSizeB <- aggregate(LOC_SIZE_B ~ SHA, data=data, FUN=sum)
  locSizeS <- aggregate(LOC_SIZE_SOLVED ~ SHA, data=data, FUN=sum)
  astSizeA <- aggregate(AST_SIZE_A ~ SHA, data=data, FUN=sum)
  astSizeB <- aggregate(AST_SIZE_B ~ SHA, data=data, FUN=sum)
  astSizeS <- aggregate(AST_SIZE_SOLVED ~ SHA, data=data, FUN=sum)
  
  final <- merge(timeA, timeB, by="SHA")
  final <- merge(final, timeS, by="SHA")
  final <- merge(final, locAB, by="SHA")
  final <- merge(final, locAS, by="SHA")
  final <- merge(final, locBS, by="SHA")
  final <- merge(final, astAB, by="SHA")
  final <- merge(final, astAS, by="SHA")
  final <- merge(final, astBS, by="SHA")
  final <- merge(final, locSizeA, by="SHA")
  final <- merge(final, locSizeB, by="SHA")
  final <- merge(final, locSizeS, by="SHA")
  final <- merge(final, astSizeA, by="SHA")
  final <- merge(final, astSizeB, by="SHA")
  final <- merge(final, astSizeS, by="SHA")
  return(final)
}

data$TIME_TIPS <- timeBetweenTips(data)
trimmed <- data[data$TIME_TIPS <= 5*86400, ]
print(summary(trimmed$TIME_TIPS))
cat("Standard deviation: ", sd(trimmed$TIME_TIPS))
timeForCommit <- aggregate(trimmed$TIME_TIPS, list(time=trimmed$SHA), mean, simplify=TRUE)
hist(timeForCommit$x, main="Resolution time", xlab="Time (s)", breaks=50)

perCommitDf <- perCommitData(trimmed)

plotTimeMetrics(perCommitDf)
#plotWithLinearRegression(trimmed, "TIME_TIPS", "LOC_A_TO_B")