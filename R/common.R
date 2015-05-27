resultsFolder <<- "../../results"

plotWithLinearRegression <<- function(data, x, y) {
  trim <- data[data[x] > 1, ]
  trim <- trim[trim[y] > 1, ]
  plot(trim[[x]], trim[[y]], xlab=x, ylab=y)
  fit = lm(data[[y]] ~ data[[x]], data=trim)
  abline(fit, col="red")
  print(summary(fit))
  print(cor.test(trim[[x]], trim[[y]]))
}

loadData <<- function(folder) {
  
  files <- list.files(path=resultsFolder, pattern="*.csv", full.names=T, recursive=FALSE)
  data <- data.frame(SHA = character(0),
                     FILE = character(0),
                     BASE_SHA = character(0),
                     BASE_TIME = integer(0), 
                     TIME_A = integer(0),
                     TIME_B = integer(0),
                     TIME_SOLVED = integer(0),
                     LOC_A_TO_B = integer(0),
                     LOC_A_TO_SOLVED = integer(0),
                     LOC_B_TO_SOLVED = integer(0),
                     AST_A_TO_B = integer(0),
                     AST_A_TO_SOLVED = integer(0),
                     AST_B_TO_SOLVED = integer(0),
                     LOC_SIZE_A = integer(0),
                     LOC_SIZE_B = integer(0),
                     LOC_SIZE_SOLVED = integer(0),
                     AST_SIZE_A = integer(0),
                     AST_SIZE_B = integer(0),
                     AST_SIZE_SOLVED = integer(0))
  
  lapply(files, function(file) {
    fileLength = length(readLines(file))
    if (fileLength <= 1)
      return 
    else {
      fileInfo <- file.info(file)
      currentDataFile <- read.csv(file, header=T, sep=',', blank.lines.skip=T, as.is=T)
      data <<- rbind(data, currentDataFile)
    }
  })
  
  return(data)
}