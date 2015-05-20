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

plotWithLinearRegression <<- function(data, x, y) {
  trim <- data[data[x] > 1, ]
  trim <- trim[trim[y] > 1, ]
  plot(trim[[x]], trim[[y]], xlab=x, ylab=y)
  fit = lm(data[[y]] ~ data[[x]], data=trim)
  abline(fit, col="red")
  print(summary(fit))
  print(cor.test(trim[[x]], trim[[y]]))
}