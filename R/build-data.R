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

total = nrow(buildData)
all = nrow(buildData.validParents)
testFails = nrow(buildData.testFails)
buildFails = nrow(buildData.buildFails)
mergeFails = nrow(buildData.mergeFails)
mergeFails.valid = nrow(buildData.mergeFails.valid)
pass = nrow(buildData.pass)
print(paste("Test Fails: ", round(testFails/all*100, 2), "%", sep=""))
print(paste("Build Fails: ", round(buildFails/all*100, 2), "%", sep=""))
print(paste("Merge Fails: ", round(mergeFails/total*100, 2), "%", sep=""))
print(paste("Merge Fails (valid): ", round(mergeFails.valid/all*100,2), "%", sep=""))
print(paste("Successful: ", round(pass/all*100, 2), "%", sep=""))

invalid = buildData[buildData$Parent1 != "pass" | buildData$Parent2 != "pass", ]
pass.invalid = invalid[invalid$Merge == "pass", ]

gitData = read.csv("../../results/build-data/git.csv", header=FALSE)
setnames(gitData, c("SHA", "Parent1", "Parent2", "Merge"))
gitData.valid = gitData[gitData$Parent1 == "pass" & gitData$Parent2 == "pass", ]
gitData.mergeFails.overall = gitData[gitData$Merge == "text", ]
gitData.mergeFails = gitData.valid[gitData.valid$Merge == "text", ]
gitData.buildFails = gitData.valid[gitData.valid$Merge == "build", ]
gitData.testFails = gitData.valid[gitData.valid$Merge == "test", ]
gitData.pass = gitData.valid[gitData.valid$Merge == "pass", ]
all = nrow(gitData)
valid = nrow(gitData.valid)
git.mergeFails.overall = nrow(gitData.mergeFails.overall)
git.mergeFails.valid = nrow(gitData.mergeFails)
git.buildFails = nrow(gitData.buildFails)
git.testFails = nrow(gitData.testFails)
git.pass = nrow(gitData.pass)
print("========Git Data=========")
print(paste("Merge fails (overall): ", round(git.mergeFails.overall/all*100, 2), "%", sep=""))
print(paste("Merge fails (valid): " , round(git.mergeFails.valid/valid*100, 2), "%", sep=""))
print(paste("Build Fails: ", round(git.buildFails/valid*100, 2), "%", sep=""))
print(paste("Test Fails: ", round(git.testFails/valid*100, 2), "%", sep=""))
print(paste("Succesful: ", round(git.pass/valid*100, 2), "%", sep=""))