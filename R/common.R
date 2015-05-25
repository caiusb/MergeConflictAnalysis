plotWithLinearRegression <<- function(data, x, y) {
  trim <- data[data[x] > 1, ]
  trim <- trim[trim[y] > 1, ]
  plot(trim[[x]], trim[[y]], xlab=x, ylab=y)
  fit = lm(data[[y]] ~ data[[x]], data=trim)
  abline(fit, col="red")
  print(summary(fit))
  print(cor.test(trim[[x]], trim[[y]]))
}