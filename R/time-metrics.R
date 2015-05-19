plotTimeMetrics <<- function(data) {
  data$RESOLUTION_TIME <- data$TIME_SOLVED - data$TIME_B
  data$EFFORT <- data$LOC_A_TO_SOLVED + data$LOC_B_TO_SOLVED;
  plot(data$EFFORT, data$RESOLUTION_TIME)
  return(data)
}