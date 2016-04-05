library(cluster)
library(dplyr)
library(bigmemory)

load("../../data/commitData")

merge.classify.df <- select(commitData, SHA, LOC_A_TO_B, LOC_A_TO_SOVED, LOC_B_TO_SOLVED, NO_STATEMENTS, NO_METHODS, NO_CLASSES)
merge.classify <- merge.classify.df
merge.classify$SHA <- NULL

#merge.matrix <- as.big.matrix(dist(merge.classify))

#pam(toClassify, 10)
