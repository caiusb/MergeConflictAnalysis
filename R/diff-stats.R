resultsFolder = "../../results"

files <- list.files(path=resultsFolder, pattern="*.csv", full.names=T, recursive=FALSE)
data <- data.frame(SHA = character(0),
                   FILE = character(0),
                   TIME_A = integer(0),
                   TIME_B = integer(0),
                   TIME_SOLVED = integer(0),
                   LOC_A_TO_B = integer(0),
                   LOC_A_TO_SOLVED = integer(0),
                   LOC_B_TO_SOLVED = integer(0),
                   AST_A_TO_B = integer(0),
                   AST_A_TO_SOLVED = integer(0),
                   AST_B_TO_SOLVED = integer(0))

lapply(files, function(file) {
  fileLength = length(readLines(file))
  if (fileLength <= 1)
    return 
  else {
    print(file)
    fileInfo <- file.info(file)
    currentDataFile <- read.csv(file, header=T, sep=',', blank.lines.skip=T, as.is=T)
    data <<- rbind(data, currentDataFile)
  }
})

#Trimming the cases where I couldn't find a diff, for various reasons...
data <- data[data$LOC_A_TO_SOLVED >= 0, ]
data <- data[data$LOC_B_TO_SOLVED >= 0, ]
data <- data[data$LOC_A_TO_B >= 0, ]

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