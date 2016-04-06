library(tools)
source("common.R")

data <- loadData(resultsFolder)

write.csv(data, file=paste(resultsFolder, "/all.csv", sep=''), row.names=FALSE)