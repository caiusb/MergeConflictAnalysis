mergeDataFolder <- "../../data-cost-conc-devel-ase16/merge-data"

read <- function(f) {
	print(f)
	if (file.size(f) != 0) 
		return(read.table(f, header=TRUE, sep=",", fill=TRUE))
}

loadCSVFiles <- function(folder) {
	csvFiles <- list.files(folder, pattern="*.csv", full.names=TRUE)
	data <- lapply(csvFiles, read)

	isNotNull <- function(x) !is.null(x)
	hasProjectColumn <- function(x) ncol(x) == 40
	
	data <- Filter(isNotNull, data)
	data <- Filter(hasProjectColumn, data)
	frames <- lapply(csvFiles, read)
	mergeData <- rbindlist(frames, use.names=TRUE, fill=TRUE) 
	return(mergeData)
}

mergeData <- loadCSVFiles(mergeDataFolder)
