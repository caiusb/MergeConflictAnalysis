package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
	
	private final Repository repository;
	private static Logger logger = Logger.getLogger(NewMain.LOG_NAME);
	
	public InMemoryMerger(Repository repository) {
		this.repository = repository;
	}
	
	public CommitStatus recreateMerge(RevCommit mergeCommit) {
		logger.info("Recreating commit " + mergeCommit.getName());
		RevCommit[] parents = mergeCommit.getParents();
		if (parents.length < 2)
			throw new IllegalArgumentException();
		
		try {
			return new CommitStatus(repository, mergeCommit.getName(), merge(parents[0], parents[1]), mergeCommit.getCommitTime());
		} catch (IOException e) {
			return null;
		}
	}

	public Map<String, CombinedFile> merge(RevCommit first, RevCommit second) throws IOException {
		Map<String, CombinedFile> results = new HashMap<String, CombinedFile>();
		
		RecursiveMerger recursiveMerger = (RecursiveMerger) new StrategyRecursive().newMerger(repository, true);
		
		if (first.getCommitTime() < second.getCommitTime()) {
			RevCommit tmp = second; 
			second = first;
			first = tmp;
		}		
		recursiveMerger.merge(first,second);
		
		Map<String, MergeResult<? extends Sequence>> mergeResults = recursiveMerger.getMergeResults();
		for (String touchedFile : mergeResults.keySet()) {
			MergeResult<? extends Sequence> mergeResult = mergeResults.get(touchedFile);
			CombinedFile fileResult = processMergeResult(first, second, mergeResult);
			results.put(touchedFile, fileResult);
		}
		
		return results;
	}

	private CombinedFile processMergeResult(RevCommit first, RevCommit second, MergeResult<? extends Sequence> mergeResult) {
		List<? extends Sequence> sequences = mergeResult.getSequences();
		CombinedFile combinedFile = new CombinedFile();
		combinedFile.setATime(first.getCommitTime());
		combinedFile.setBTime(second.getCommitTime());
		mergeResult.forEach((chunk) -> processChunk(chunk, sequences, combinedFile));
		
		return combinedFile;
	}

	private void processChunk(MergeChunk mergeChunk, List<? extends Sequence> sequences, CombinedFile combinedFile) {
		ConflictState conflictState = mergeChunk.getConflictState();
		int beginIndex = mergeChunk.getBegin();
		int endIndex = mergeChunk.getEnd();
		int sequenceIndex = mergeChunk.getSequenceIndex();
		
		RawText textSequence = (RawText) sequences.get(sequenceIndex);
		String actualSequenceText = getTextForLines(textSequence, beginIndex, endIndex);
		
		ChunkOwner owner = null;
		if (conflictState.equals(ConflictState.FIRST_CONFLICTING_RANGE))
			owner = ChunkOwner.A;
		if (conflictState.equals(ConflictState.NEXT_CONFLICTING_RANGE))
			owner = ChunkOwner.B;
		if (conflictState.equals(ConflictState.NO_CONFLICT))
			owner = ChunkOwner.BOTH;
		
		combinedFile.addChunk(owner, actualSequenceText);
	}

	private String getTextForLines(RawText textSequence, int beginIndex, int endIndex) {
		String actualSequenceText = "";
		for (int i=beginIndex; i<endIndex; i++) {
			actualSequenceText += textSequence.getString(i) + "\n";
		}
		return actualSequenceText;
	}

}
