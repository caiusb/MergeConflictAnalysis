source("common.R")

nonMerge <- loadNonMerge(concat(resultsFolder, "/regular"))
nonMerge$Date <- unix2POSIXct(nonMerge$COMMIT_TIME)
nonMerge <- calculateWeekdays(nonMerge)
nonMerge <- calculateTimes(nonMerge)