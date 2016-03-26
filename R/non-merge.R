source("common.R")

resultsFolder <<- "../../results/merge-data"

loadNonMerge <- function(folder) {
  files <- listCSVFiles(folder)
  
  data <- data.frame(SHA = character(0),
                     TIME = integer(0),
                     AUTHOR = character(0))
  
  data <- readCSVFiles(files, data)
  return(data)
}


nonMerge <- loadNonMerge(concat(resultsFolder, "/regular"))
nonMerge$Date <- unix2POSIXct(nonMerge$COMMIT_TIME)
nonMerge <- calculateWeekdays(nonMerge)
nonMerge <- calculateTimes(nonMerge)