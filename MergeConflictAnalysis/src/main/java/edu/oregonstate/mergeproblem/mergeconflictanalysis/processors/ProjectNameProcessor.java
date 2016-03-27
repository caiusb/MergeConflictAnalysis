package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;

public class ProjectNameProcessor implements FileProcessor {

    @Override
    public String getHeader() {
        return "PROJECT";
    }

    @Override
    public String getData(CommitStatus status, String fileName) {
        return status.getRepository().getWorkTree().getName();
    }
}
