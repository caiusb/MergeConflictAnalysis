library(tools)

resultsFolder = "../../results"

files <<- list.files(path=resultsFolder, pattern="*.csv", full.names=T, recursive=FALSE)

data <- data.frame(PROJECT = character(0),
                   SHA = character(0),
                   FILE = character(0),
                   LOC_A_TO_B = integer(0),
                   LOC_A_TO_SOLVED = integer(0),
                   LOC_B_TO_SOLVED = integer(0),
                   AST_A_TO_B = integer(0),
                   AST_A_TO_SOLVED = integer(0),
                   AST_B_TO_SOLVED = integer(0))

lapply(files, function(file) {
  fileLength = length(readLines(file))
  if (fileLength <= 2)
    return 
  else {
    fileInfo <- file.info(file)
    currentDataFile <- read.csv(file, header=T, sep=',', blank.lines.skip=T, as.is=T)
    project <- basename(file_path_sans_ext(file))
    currentDataFile$PROJECT <- project
    data <<- rbind(data, currentDataFile)
  }
})

write.csv(data, file=paste(resultsFolder, "/all.csv", sep=''), row.names=FALSE)