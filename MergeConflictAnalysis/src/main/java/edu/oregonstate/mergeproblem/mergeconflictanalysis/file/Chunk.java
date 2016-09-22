package edu.oregonstate.mergeproblem.mergeconflictanalysis.file;

public class Chunk {

    public enum ChunkSource {
        A, B, BASE, NONE;
    }

    private ChunkOwner owner;
    private ChunkSource source;
    private String content;
    private int beginLine;
    private int endLine;

    public Chunk(ChunkOwner owner, String content, int beginLine, int endLine, ChunkSource source) {
        this.owner = owner;
        this.content = content;
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.source = source;
    }

    public Chunk(ChunkOwner owner, String content, int beginLine, int endLine) {
        this(owner, content, beginLine, endLine, ChunkSource.NONE);
    }

    public Chunk(ChunkOwner owner, String content) {
        this(owner, content, 0, 0);
    }

    public boolean isOwner(ChunkOwner a) {
        if (owner.equals(ChunkOwner.BOTH))
            return true;

        return owner.equals(a);
    }

    public ChunkSource getSource() {
        return source;
    }

    public boolean isExclusiveOwner(ChunkOwner a) {
        return owner.equals(a);
    }

    public boolean isConflictChunk() {
        return owner == ChunkOwner.A || owner == ChunkOwner.B;
    }

    public String getContent() {
        return content;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public boolean hasLine(int line) {
        return line >= beginLine && line < endLine;
    }
}