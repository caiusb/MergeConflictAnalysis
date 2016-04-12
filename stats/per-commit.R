library(dplyr)

mergeData <- load("../../data/mergeData")

commitData <- mergeData %>% group_by(SHA) %>%
  summarise(NO_FILES=length(unique(FILE)),
            LOC_A_TO_B=sum(LOC_A_TO_B),
            LOC_A_TO_SOVED=sum(LOC_A_TO_SOLVED),
            LOC_B_TO_SOLVED=sum(LOC_B_TO_SOLVED),
            AST_A_TO_B=sum(AST_A_TO_B),
            AST_A_TO_SOLVED=sum(AST_A_TO_SOLVED),
            AST_B_TO_SOLVED=sum(AST_B_TO_SOLVED),
            SHA_A=first(SHA_A),
            SHA_B=first(SHA_B),
            BASE_SHA=first(BASE_SHA),
            NO_METHODS=sum(NO_METHODS),
            NO_CLASSES=sum(NO_CLASSES),
            NO_STATEMENTS=sum(NO_STATEMENTS),
            TIME_A=min(TIME_A),
            TIME_B=min(TIME_B),
            BASE_TIME=min(BASE_TIME),
            PROJECT=first(PROJECT))

save(commitData, file="commitData", ascii=TRUE, compress=TRUE)
