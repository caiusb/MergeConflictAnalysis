package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.Sequence;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeChunk;
import org.eclipse.jgit.merge.MergeChunk.ConflictState;
import org.eclipse.jgit.merge.MergeResult;
import org.eclipse.jgit.merge.RecursiveMerger;
import org.eclipse.jgit.merge.StrategyRecursive;
import org.eclipse.jgit.revwalk.RevCommit;

public class InMemoryMerger {
	
	private Repository repository;
	private List<String> names;

	public InMemoryMerger(Repository repository) {
		this.repository = repository;
		names = Arrays.asList(new String[]{"base", "A", "B"});
	}

	public void merge(RevCommit first, RevCommit second) throws IOException {
		RecursiveMerger recursiveMerger = (RecursiveMerger) new StrategyRecursive().newMerger(repository, true);
		recursiveMerger.merge(first, second);
		
		Map<String, MergeResult<? extends Sequence>> mergeResults = recursiveMerger.getMergeResults();
		for (String touchedFile : mergeResults.keySet()) {
			MergeResult<? extends Sequence> mergeResult = mergeResults.get(touchedFile);
			processMergeResult(mergeResult);
		}
	}

	private void processMergeResult(MergeResult<? extends Sequence> mergeResult) {
		List<? extends Sequence> sequences = mergeResult.getSequences();
		mergeResult.forEach((chunk) -> processChunk(chunk, sequences));
	}

	private void processChunk(MergeChunk mergeChunk, List<? extends Sequence> sequences) {
		ConflictState conflictState = mergeChunk.getConflictState();
		int beginIndex = mergeChunk.getBegin();
		int endIndex = mergeChunk.getEnd();
		int sequenceIndex = mergeChunk.getSequenceIndex();
		
		if (conflictState.equals(ConflictState.FIRST_CONFLICTING_RANGE))
			System.out.println(">>>>" + names.get(sequenceIndex));
		if (conflictState.equals(ConflictState.NEXT_CONFLICTING_RANGE))
			System.out.println("=====" + names.get(sequenceIndex));
		
		RawText textSequence = (RawText) sequences.get(sequenceIndex);
		String actualSequenceText = getTextForLines(textSequence, beginIndex, endIndex);

		System.out.println(actualSequenceText);
	}

	private String getTextForLines(RawText textSequence, int beginIndex, int endIndex) {
		String actualSequenceText = "";
		for (int i=beginIndex; i<endIndex; i++) {
			actualSequenceText += textSequence.getString(i) + "\n";
		}
		return actualSequenceText;
	}

}
