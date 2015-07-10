source('common.R')

data <- loadData(resultsFolder)

calculateWeekdays <- function(data) {
  data$Weekday <- weekdays(data$Date)
  data$Weekday <- factor(data$Weekday, levels = c("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"))
  return(data)
}


calculateTimes <- function(data) {
  hours <- format(data$Date, format="%H")
  getTimeOfDay <- function(hour) {
    hour <- as.numeric(hour)

    if (is.na(hour))
      return("Unknown")
    if (hour >= 0 & hour < 8)
      return("Night")
    if (hour >= 8 & hour < 12)
      return("Morning")
    if (hour >= 12 & hour < 18)
      return("Afternoon")
    if (hour >= 18 & hour <= 23)
      return("Evening")
    
    return("Unknown")
  }
  data$TimeOfDay <- lapply(hours, getTimeOfDay)
  data$TimeOfDay <- factor(data$TimeOfDay, levels = c("Morning", "Afternoon", "Evening", "Night", "Unknown"))
}

commitData <- createCommitData(data)

successfulCommitData <- commitData[commitData$IS_CONFLICT == FALSE, ]
conflictingCommitData <- commitData[commitData$IS_CONFLICT == TRUE, ]

weekdayData <- calculateWeekdays(successfulCommitData)
barplot(table(weekdayData$Weekday))
title(main="Successful merge commits")

weekdayData <- calculateWeekdays(conflictingCommitData)
barplot(table(weekdayData$Weekday))
title(main="Conflicting merge commits")

timeOfDayData <- calculateTimes(commitData)
barplot(table(timeOfDayData))
title(main="When developers merge code")