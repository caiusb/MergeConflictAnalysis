package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	public String getData(CommitStatus status, String fileName) {
		return processors.stream()
				.map((processor) -> processor.getData(status, fileName))
				.collect(Collectors.joining(","));
	}

}
