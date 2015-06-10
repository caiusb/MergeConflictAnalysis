library(tools)

resultsFolder <<- "../../results"

plotWithLinearRegression <<- function(data, x, y) {
  trim <- trimNegativeValues(data, x)
  trim <- trimNegativeValues(data, y)
  plot(trim[[x]], trim[[y]], xlab=x, ylab=y)
  fit = lm(data[[y]] ~ data[[x]], data=trim)
  abline(fit, col="red")
  print(summary(fit))
  print(cor.test(trim[[x]], trim[[y]]))
}

trimNegativeValues <<- function(data, x) {
  return(data[data[x] >= 0, ])
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
      project <- basename(file_path_sans_ext(file))
      currentDataFile$PROJECT <- project
      data <<- rbind(data, currentDataFile)
    }
  })
  
  #removing file add and delete. They are not interesting to me
  data <- data[data$LOC_SIZE_A > 1, ]
  data <- data[data$LOC_SIZE_B > 1, ]
  data <- data[data$LOC_SIZE_SOLVED > 1, ]
  
  return(data)
}

createCommitData <<- function(data) {
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