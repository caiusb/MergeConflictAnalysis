source('common.R')

commitData <- loadCommitData(commitFolder)
pullReq <- loadPullReqData(prFolder)

mergedPullReq <- pullReq[pullReq$MERGED == TRUE, ]
mergedPullReq$SOLVE_TIME <- mergedPullReq$MERGED_TIME - mergedPullReq$CREATED_TIME
#trimmed <- mergedPullReq[mergedPullReq$SOLVE_TIME <= 7*60*60, ] #7 days solve time
#hist(trimmed$SOLVE_TIME, breaks=50)
mergePullRequestAndCommitData <- function(commitData, pullReqData) {
  return(merge(commitData, pullReqData, by = c("SHA"), all.x = TRUE, all.y = FALSE))
}

merged <<- mergePullRequestAndCommitData(commitData, mergedPullReq)
write.csv(merged, "../../results/mergedPR.csv")