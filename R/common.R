library(tools)
require(data.table)

resultsFolder <<- "../../results/merge-data"
prFolder <<- "../../results/pr-summary"

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

listCSVFiles <- function(folder) {
  return(list.files(path=folder, pattern="*.csv", full.names=T, recursive=FALSE))
}

readCSVFiles <- function(files, dataFrame) {
  lapply(files, function(file) {
    fileLength = length(readLines(file))
    if (fileLength <= 1)
      return 
    else {
      print(cat('Processing: ', file))
      fileInfo <- file.info(file)
      currentDataFile <- fread(file, header=T, sep=',')
      project <- basename(file_path_sans_ext(file))
      currentDataFile$PROJECT <- project
      if (!("MERGED_IN_MASTER" %in% currentDataFile))
        currentDataFile$MERGED_IN_MASTER <- NA
      dataFrame <<- rbindlist(list(dataFrame, currentDataFile), fill=TRUE)
    }
  })
  return(dataFrame)
}

loadData <<- function(folder) {
  files <- listCSVFiles(folder)
  
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
                     AST_SIZE_SOLVED = integer(0),
                     IS_CONFLICT = logical(0),
                     NO_METHODS = integer(0),
                     NO_CLASSES = integer(0),
                     NO_ADD = integer(0),
                     NO_UPDATE = integer(0),
                     NO_DELETE = integer(0),
                     LOC_A_BEFORE_SIZE = integer(0),
                     LOC_B_BEFORE_SIZE = integer(0),
                     LOC_DIFF_BEFORE = integer(0),
                     AST_A_BEFORE_SIZE = integer(0),
                     AST_B_BEFORE_SIZE = integer(0),
                     AST_DIFF_BEFORE = integer(0),
                     COUPLING_CHANGE = integer(0),
                     CYCLO_CHANGE = integer(0),
                     NO_AUTHORS = integer(0),
                     MERGED_IN_MASTER = logical(0))
  
  print("Loading data")
  data <- readCSVFiles(files, data)
  
  #removing file add and delete. They are not interesting to me
  print("Removing bad data points")
  data <- data[data$LOC_SIZE_A > 1, ]
  data <- data[data$LOC_SIZE_B > 1, ]
  data <- data[data$LOC_SIZE_SOLVED > 1, ]
  
  data$PROJECT <- factor(data$PROJECT)
  
  #calculate commit data
  print("Calculating the dates")
  data$Date <- unix2POSIXct(data$TIME_SOLVED)
  data$IS_CONFLICT <- ifelse(data$IS_CONFLICT == "true", TRUE, FALSE)
  data$MERGED_IN_MASTER <- ifelse(data$MERGED_IN_MASTER == "True", TRUE, FALSE)
  
  return(data)
}

createCommitData <<- function(data) {
  
  print("Grouping data by commits")
  or <- function(vector) {
    
    oneElementOr <- function(element, accumulated) {
      element  <- as.logical(element)
      accumulated <- as.logical(accumulated)
      return(element | accumulated)
    }
    
    return(Reduce(oneElementOr, vector, FALSE))
  }
  
  justOne <- function(vector) {
    return(vector[1])
  }
  
  noFiles <- aggregate(FILE ~ SHA, data=data, FUN=length)
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
  noMethods <- aggregate(NO_METHODS ~ SHA, data=data, FUN=sum)
  noClasses <- aggregate(NO_CLASSES ~ SHA, data=data, FUN=sum)
  noAdd <- aggregate(NO_ADD ~ SHA, data=data, FUN=sum)
  noUpdate <- aggregate(NO_UPDATE ~ SHA, data=data, FUN=sum)
  noDelete <- aggregate(NO_DELETE ~ SHA, data=data, FUN=sum)
  locDiffBefore <- aggregate(LOC_DIFF_BEFORE ~ SHA, data=data, FUN=sum)
  isConflict <- aggregate(IS_CONFLICT ~ SHA, data=data, FUN=or)
  date <- aggregate(Date ~ SHA, data=data, FUN=mean)
  project <- aggregate(PROJECT ~ SHA, data=data, FUN=justOne)
  noAuthors <- aggregate(NO_AUTHORS ~ SHA, data=data, FUN=mean)
  coupling <- aggregate(COUPLING_CHANGE ~ SHA, data=data, FUN=sum)
  cyclo <- aggregate(CYCLO_CHANGE ~ SHA, data=data, FUN=sum)
  mergedInMaster <- aggregate(MERGED_IN_MASTER ~ SHA, data=data, FUN=or)
  
  final <- merge(noFiles, timeA, by="SHA")
  final <- merge(final, timeB, by="SHA")
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
  final <- merge(final, noMethods, by="SHA")
  final <- merge(final, noClasses, by="SHA")
  final <- merge(final, noAdd, by="SHA")
  final <- merge(final, noUpdate, by="SHA")
  final <- merge(final, noDelete, by="SHA")
  final <- merge(final, locDiffBefore, by="SHA")
  final <- merge(final, isConflict, by="SHA")
  final <- merge(final, date, by="SHA")
  final <- merge(final, project, by="SHA")
  final <- merge(final, noAuthors, by="SHA")
  final <- merge(final, coupling, by="SHA")
  final <- merge(final, cyclo, by="SHA")
  final <- merge(final, mergedInMaster, by="SHA")
  
  final$PROJECT <- as.factor(final$PROJECT)
  
  return(final)
}

unix2POSIXct <- function(time) as.POSIXct(time, origin="1970-01-01", tz="GMT")

getConflictingMerges <- function(data) return(data[data$IS_CONFLICT == "true", ])
getSuccessfulMerges <- function(data) return(data[data$IS_CONFLICT == "false", ])

loadNonMerge <- function(folder) {
  files <- listCSVFiles(folder)
  
  data <- data.frame(SHA = character(0),
                     TIME = integer(0),
                     AUTHOR = character(0))

  data <- readCSVFiles(files, data)
  return(data)
}

concat <- function(a, b) paste(a, b, colapse=NULL, sep='')

loadPullReqData <- function(folder) {
  files <- listCSVFiles(folder)
  
  data <- data.frame(PR = integer(0),
                     MERGEABLED = logical(0),
                     MERGED = logical(0),
                     CLOSED = logical(0),
                     CREATED_TIME = integer(0),
                     MERGED_TIME = integer(0),
                     SHA = character(0))

  data <- readCSVFiles(files, data)
  
  data$MERGEABLE <- ifelse(data$MERGEABLE == "True", TRUE, FALSE)
  data$MERGED <- ifelse(data$MERGED == "True", TRUE, FALSE)
  data$CLOSED <- ifelse(data$CLOSED == "True", TRUE, FALSE)
  
  data$PROJECT <- factor(data$PROJECT)
  
  return(data)
}
