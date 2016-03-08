source("common.R")
require(plyr)

buildData <- data.frame(commit = character(0),
                         parent1 = character(0),
                         parent2 = character(0),
                         merge = character(0))

files <- listCSVFiles("../../results/build-data/results")
print(files)

buildData <- readCSVFiles(files, data.frame(), F)


mergeData <- read.csv("../../results/build-data-no-merges.csv", header=T, sep=',')

sanityCheck <- function(buildData, mergeData) {
  f <- factor(buildData$PROJECT)
  t <- table(buildData$PROJECT)
  pass = TRUE
  for(project in row.names(t)) {
    noMerges = mergeData[mergeData$PROJECT == project, ]$NO_MERGES
    if (t[project] < noMerges) {
      print(paste("Failed for", project, ": ", noMerges, " merges and ", t[project], " builds", sep=""))
      pass = FALSE
    }
  }
  
  return(pass)
}

if(sanityCheck(buildData, mergeData) == FALSE)
  print("Oh fuck!")

buildData.validParents = buildData[buildData$V2 == "pass" & buildData$V3 == "pass", ]
buildData.testFails = buildData.validParents[buildData.validParents$V4 == "test", ]
buildData.buildFails = buildData.validParents[buildData.validParents$V4 == "build", ]
buildData.mergeFails = buildData.validParents[buildData.validParents$V4 == "text", ]
buildData.pass = buildData.validParents[buildData.validParents$V4 == "pass", ]

all = nrow(buildData.validParents)
testFails = nrow(buildData.testFails)
buildFails = nrow(buildData.buildFails)
mergeFails = nrow(buildData.mergeFails)
pass = nrow(buildData.pass)
print(paste("Test Fails: ", testFails/all*100, "%", sep=""))
print(paste("Build Fails: ", buildFails/all*100, "%", sep=""))
print(paste("Merge Fails: ", mergeFails/all*100, "%", sep=""))
print(paste("Successful: ", pass/all*100, "%", sep=""))