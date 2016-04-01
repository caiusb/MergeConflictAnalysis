library(cluster)
library(dplyr)

load("../../data/commitData")

toClassify <- select(commitData, SHA, LOC_A_TO_B, LOC_A_TO_SOVED, LOC_B_TO_SOLVED, NO_STATEMENTS, NO_METHODS, NO_CLASSES)

#pam(toClassify, 10)
