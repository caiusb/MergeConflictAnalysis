mergeDataFolder <- "../../data-cost-conc-devel-ase16/merge-data"

read <- function(f) {
	if (file.size(f) != 0) 
		return(read.csv(f, header=TRUE, sep=","))
}

loadCSVFiles <- function(folder) {
	csvFiles <- list.files(folder, pattern="*.csv", full.names=TRUE)
	data <- lapply(csvFiles, read)

	isNotNull <- function(x) !is.null(x)
	hasProjectColumn <- function(x) ncol(x) == 40
	
	data <- Filter(isNotNull, data)
	data <- Filter(hasProjectColumn, data)
	mergeData <- do.call(rbind, data)
	return(mergeData)
}

mergeData <- loadCSVFiles(mergeDataFolder)
