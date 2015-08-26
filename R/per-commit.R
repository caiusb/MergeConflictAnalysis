source('common.R')

data <- loadData(resultsFolder)
commitData <- createCommitData(data)

factors <- levels(factor(commitData$PROJECT))
frames <- split(commitData, commitData$PROJECT)

lapply(factors, function(factor) {
  file <- concat(concat(commitFolder, as.character(factor)), ".csv")
  print(file)
  project <- frames[[factor]]
  write.csv(project, file, sep='')
})
