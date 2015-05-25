source('common.R')

plotTimeMetrics <<- function(data) {
  data$RESOLUTION_TIME <- data$TIME_SOLVED - data$TIME_B
  data$EFFORT <- calculateEffort(data)
  print(data$EFFORT)
  plotWithLinearRegression(data, 'EFFORT','RESOLUTION_TIME')
  return(data)
}

calculateEffort <- function(data) {
  averageConflictSize <- (data$LOC_SIZE_A + data$LOC_SIZE_B)/2
  deviationFromDiagonal <- sqrt((data$LOC_A_TO_SOLVED^2) + (data$LOC_B_TO_SOLVED)^2)
  effort <- deviationFromDiagonal
  return(effort)
}
