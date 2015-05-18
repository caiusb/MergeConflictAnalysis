plotDistanceMetrics <<- function(data) {
  hist(data$LOC_A_TO_SOLVED, breaks=20)
  hist(data$LOC_B_TO_SOLVED, breaks=20)
  hist(data$LOC_A_TO_B, breaks=20)
  
  hist(data$AST_A_TO_SOLVED, breaks=200)
  hist(data$AST_B_TO_SOLVED, breaks=200)
  hist(data$AST_A_TO_B, breaks=200)
  
  plot(data$LOC_A_TO_SOLVED, data$LOC_B_TO_SOLVED)
  plot(data$LOC_A_TO_B, data$LOC_A_TO_SOLVED)
  plot(data$LOC_A_TO_B, data$LOC_B_TO_SOLVED)
  
  plot(data$AST_A_TO_SOLVED, data$AST_B_TO_SOLVED)
  plot(data$AST_A_TO_B, data$AST_A_TO_SOLVED)
  plot(data$AST_A_TO_B, data$AST_B_TO_SOLVED)
}