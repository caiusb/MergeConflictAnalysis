source('common.R')
source('analysis.R')

resultsFolder <- "../../data-cost-conc-devel-ase16/merge-data/"
commitFolder <- "../../data-cost-conc-devel-ase16/per-commit/"

data <- loadCSVFiles(resultsFolder)
print(summary(data))
commitData <- createCommitData(data)

factors <- levels(factor(commitData$PROJECT))
frames <- split(commitData, commitData$PROJECT)

lapply(factors, function(factor) {
  file <- concat(concat(commitFolder, as.character(factor)), ".csv")
  print(file)
  project <- frames[[factor]]
  write.csv(project, file, sep='')
})
