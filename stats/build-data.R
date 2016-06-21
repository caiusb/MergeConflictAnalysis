source("common.R")
require(plyr)

buildData <- data.frame(commit = character(0),
                         parent1 = character(0),
                         parent2 = character(0),
                         merge = character(0))
print(getwd())
files <- list.files("/Users/caius/osu/TheMergingProblem/data/build-data/results", pattern="*.csv", full.names=TRUE)

read <- function(f) {
  if (file.size(f) != 0) {
    frame = read.csv(f, header=FALSE, sep=",")
    frame$Project = basename(file_path_sans_ext(f))
    return(frame)
  }
}

loadCSVFiles <- function(folder) {
  csvFiles <- list.files(folder, pattern="*.csv", full.names=TRUE)
  data <- lapply(csvFiles, read)
  
  frames = lapply(files, read)
  buildData <- rbindlist(frames, use.names=TRUE, fill=TRUE)
  return(buildData)
}

buildData <- loadCSVFiles(files)

setnames(buildData, c('Commit', 'Parent1', 'Parent2', 'Merge', "Project"))

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

#if(sanityCheck(buildData, mergeData) == FALSE)
#  print("Oh fuck!")

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
pass.unbelievable = buildData[buildData$Parent1 != "pass" & buildData$Parent2 != "pass" & buildData$Merge == "pass", ]

gitData = read.csv("../../results/build-data/git.csv", header=FALSE)
perlData = read.csv("../../results/build-data/perl5-v2.csv", header=FALSE)
voldemortData = read.csv("../../results/build-data/voldermort.csv", header=FALSE)
columnNames = c("SHA", "Parent1", "Parent2", "Merge")
setnames(gitData, columnNames)
setnames(perlData, columnNames)
setnames(voldemortData, columnNames)

gitData.valid = gitData[gitData$Parent1 == "pass" & gitData$Parent2 == "pass", ]
gitData.mergeFails.overall = gitData[gitData$Merge == "text", ]
gitData.mergeFails = gitData.valid[gitData.valid$Merge == "text", ]
gitData.buildFails = gitData.valid[gitData.valid$Merge == "build", ]
gitData.buildFails.overall = gitData[gitData$Merge == "build", ]
gitData.testFails = gitData.valid[gitData.valid$Merge == "test", ]
gitData.testFails.overall = gitData[gitData$Merge == "test", ]
gitData.pass = gitData.valid[gitData.valid$Merge == "pass", ]
gitData.pass.overall = gitData[gitData$Merge == "pass", ]
all = nrow(gitData)
valid = nrow(gitData.valid)
git.mergeFails.overall = nrow(gitData.mergeFails.overall)
git.buildFails.overall = nrow(gitData.buildFails.overall)
git.testFails.overall = nrow(gitData.testFails.overall)
git.pass.overall = nrow(gitData.pass.overall)
git.mergeFails.valid = nrow(gitData.mergeFails)
git.buildFails = nrow(gitData.buildFails)
git.testFails = nrow(gitData.testFails)
git.pass = nrow(gitData.pass)
print("========Git Data=========")
print(paste("# of merges: ", all, sep=""))
print(paste("# of valid merges: ", valid, sep=""))
print(paste("# of invalid merges: ", all-valid, sep=""))
print(paste("Merge fails (overall): ", round(git.mergeFails.overall/all*100, 2), "%", sep=""))
print(paste("Build fails (overall): ", round(git.buildFails.overall/all*100, 2), "%", sep=""))
print(paste("Test fails (overall): ", round(git.testFails.overall/all*100, 2), "%", sep=""))
print(paste("Successful (overall): ", round(git.pass.overall/all*100, 2), "%", sep=""))
print(paste("Merge fails (valid): " , round(git.mergeFails.valid/valid*100, 2), "%", sep=""))
print(paste("Build Fails: ", round(git.buildFails/valid*100, 2), "%", sep=""))
print(paste("Test Fails: ", round(git.testFails/valid*100, 2), "%", sep=""))
print(paste("Succesful: ", round(git.pass/valid*100, 2), "%", sep=""))

perlData.valid = perlData[perlData$Parent1 == "pass" && perlData$Parent2 == "pass", ]

voldemortData.valid = voldemortData[voldemortData$Parent1 == "pass" && voldemortData$Parent2 == "pass", ]
voldemortData.pass = voldemortData.valid[voldemortData.valid$Merge == "pass", ]
voldemortData.buildFails = voldemortData.valid[voldemortData.valid$Merge == "build", ]
voldemortData.testFails = voldemortData.valid[voldemortData.valid$Merge == "test", ]
voldemortData.mergeFails = voldemortData.valid[voldemortData.valid$Merge == "text", ]
voldemort.all = nrow(voldemortData)
voldemort.valid = nrow(voldemortData.valid)
voldemort.pass = nrow(voldemortData.pass)
voldemort.build = nrow(voldemortData.buildFails)
voldemort.test = nrow(voldemortData.testFails)
voldemort.text = nrow(voldemortData.mergeFails)
#print("")
#print("======Voldemort Data =======")
#print(paste("# of merges: ", voldemort.all, sep=""))
#print(paste("# of valid merges: ", voldemort.valid, sep=""))
#print(paste("Merge fails: ", round(voldemort.text/voldemort.valid, 2), sep=""))