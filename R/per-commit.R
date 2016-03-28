source('common.R')
source('analysis.R')

library(dplyr)

resultsFolder <- "../../data-cost-conc-devel-ase16/merge-data/"
commitFolder <- "../../data-cost-conc-devel-ase16/per-commit/"

mergeData <- loadCSVFiles(resultsFolder)
print(summary(data))

commitData <- group_by(mergeData, SHA) 

factors <- levels(factor(commitData$PROJECT))
frames <- split(commitData, commitData$PROJECT)

lapply(factors, function(factor) {
  file <- concat(concat(commitFolder, as.character(factor)), ".csv")
  print(file)
  project <- frames[[factor]]
  write.csv(project, file, sep='')
})