package edu.oregonstate.mergeproblem.mergeconflictanalysis.file;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CombinedFile {

	private List<Chunk> chunks = new ArrayList<Chunk>();
	private int aTime;
	private int bTime;

	public CombinedFile addChunk(ChunkOwner owner, String content) {
		return addChunk(owner, content, 0, 0);
	}

	public CombinedFile addChunk(ChunkOwner owner, String content, int beginLine, int endLine, Chunk.ChunkSource source) {
		chunks.add(new Chunk(owner, content, beginLine, endLine, source));
		return this;
	}

	public CombinedFile addChunk(ChunkOwner owner, String content, int beginLine, int endLine) {
		return addChunk(owner, content, beginLine, endLine, Chunk.ChunkSource.NONE);
	}

	public String getVersion(ChunkOwner owner) {
		String version = chunks.stream().filter((chunk) -> chunk.isOwner(owner))
				.map((chunk) -> chunk.getContent())
				.collect(Collectors.joining());
		return version;
	}
	
	public void setATime(int time) {
		this.aTime = time;
	}

	public int getATime() {
		return aTime;
	}

	public void setBTime(int time) {
		this.bTime = time;
	}

	public int getBTime() {
		return bTime;
	}

	public List<Integer> getConflictingLines() {
		Stream<Chunk> conflictingChunks = chunks.stream().filter(c -> c.isConflictChunk());
		return conflictingChunks.flatMap(c -> IntStream.range(c.getBeginLine(), c.getEndLine()).boxed()).distinct()
				.collect(Collectors.toList());
	}

	public List<Chunk> getChunksForLine(int line) {
		return chunks.stream().filter(c -> c.hasLine(line)).collect(Collectors.toList());
	}

	public List<Chunk> getChunksForOwner(ChunkOwner owner) {
		return chunks.stream().filter(c -> c.isExclusiveOwner(owner)).collect(Collectors.toList());
	}
}