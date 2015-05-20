package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class CompositeProcessor implements FileProcessor {
	
	private List<FileProcessor> processors = new ArrayList<FileProcessor>();
	
	public void addProcessor(FileProcessor processor) {
		processors.add(processor);
	}

	@Override
	public String getHeader() {
		return processors.stream()
				.map((processor) -> processor.getHeader())
				.collect(Collectors.joining(","));
	}

	@Override
	public String getDataForMerge(CommitStatus status, String fileName) {
		return processors.stream()
				.map((processor) -> processor.getDataForMerge(status, fileName))
				.collect(Collectors.joining(","));
	}

}
