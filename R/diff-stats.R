resultsFolder = "../../results"

files <- list.files(path=resultsFolder, pattern="*.json", full.names=T, recursive=FALSE)
data <- data.frame(SHA = character(0),
                   FILE = character(0),
                   A_TO_B = integer(0),
                   A_TO_SOLVED = integer(0),
                   B_TO_SOLVED = integer(0))

lapply(files, function(file) {
  fileInfo <- file.info(file)
  currentDataFile <- read.csv(file, header=T, sep=',', blank.lines.skip=T, as.is=T)
  data <<- rbind(data, currentDataFile)
})

#Trimming the cases where I couldn't find a Diff
data <- data[data$A_TO_SOLVED >= 0, ]
data <- data[data$B_TO_SOLVED >= 0, ]
data <- data[data$A_TO_B >= 0, ]

plot(data$A_TO_SOLVED, data$B_TO_SOLVED)
plot(data$A_TO_B, data$A_TO_SOLVED)
plot(data$A_TO_B, data$B_TO_SOLVED)