source("common.R")
require(plyr)

buildData <- data.frame(commit = character(0),
                         parent1 = character(0),
                         parent2 = character(0),
                         merge = character(0))

files <- listCSVFiles("../../results/build-data/results")
print(files)

buildData <- readCSVFiles(files, data.frame(), F)
setnames(buildData, c('Commit', 'Parent1', 'Parent2', 'Merge', 'PROJECT'))

mergeData <- read.csv("../../results/build-data-no-merges.csv", header=T, sep=',')

sanityCheck <- function(buildData, mergeData) {
  f <- factor(buildData$PROJECT)
  t <- table(buildData$PROJECT)
  pass = TRUE
  for(project in row.names(t)) {
    noMerges = mergeData[mergeData$PROJECT == project, ]$NO_MERGES
    if (t[project] < noMerges) {
      print(paste("Failed for ", project, ": ", noMerges, " merges and ", t[project], " builds", sep=""))
      pass = FALSE
    }
  }
  
  return(pass)
}

if(sanityCheck(buildData, mergeData) == FALSE)
  print("Oh fuck!")

buildData.validParents = buildData[buildData$Parent1 == "pass" & buildData$Parent2 == "pass", ]
buildData.testFails = buildData.validParents[buildData.validParents$Merge == "test", ]
buildData.buildFails = buildData.validParents[buildData.validParents$Merge == "build", ]
buildData.mergeFails = buildData[buildData$Merge == "text", ]
buildData.mergeFails.valid = buildData.validParents[buildData.validParents$Merge == "text", ]
buildData.pass = buildData.validParents[buildData.validParents$Merge == "pass", ]

all = nrow(buildData.validParents)
testFails = nrow(buildData.testFails)
buildFails = nrow(buildData.buildFails)
mergeFails = nrow(buildData.mergeFails)
pass = nrow(buildData.pass)
print(paste("Test Fails: ", round(testFails/all*100, 2), "%", sep=""))
print(paste("Build Fails: ", round(buildFails/all*100, 2), "%", sep=""))
print(paste("Merge Fails: ", round(mergeFails/all*100, 2), "%", sep=""))
print(paste("Successful: ", round(pass/all*100, 2), "%", sep=""))