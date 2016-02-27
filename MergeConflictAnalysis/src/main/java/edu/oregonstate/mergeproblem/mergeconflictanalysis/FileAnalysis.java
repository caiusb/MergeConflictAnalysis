package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileAnalysis {
    CompositeProcessor processor;

    public FileAnalysis() {
    }

    void doAnalysis(Main.Config config, BufferedOutputStream outputStream) throws Exception {
        initializeProcessor();
        for (String repositoryPath : config.repositories) {
            String projectName = Paths.get(repositoryPath).getFileName().toString();

            Main.logger.info("Recreating the commits for " + repositoryPath);
            List<CommitStatus> statuses = recreateMergesInRepository(repositoryPath);
            Main.logger.info("Processing results for " + repositoryPath);
            processResultsAndWriteToStream(statuses, outputStream);
            outputStream.flush();
            outputStream.close();
            if (config.vizFolder != null)
                generateDiffs(projectName, statuses, config);
        }
    }

    void initializeProcessor() {
        processor = new CompositeProcessor();
        processor.addProcessor(new BasicDataProcessor());
        processor.addProcessor(new MergeBaseProcessor());
        processor.addProcessor(new LOCFileProcessor());
        processor.addProcessor(new ASTFileProcessor());
        processor.addProcessor(new LOCSizeProcessor());
        processor.addProcessor(new ASTSizeProcessor());
        processor.addProcessor(new IsConflictProcessor());
        processor.addProcessor(new ModifiedProgramElementsProcessor());
        processor.addProcessor(new OperationCounterProcessor());
        processor.addProcessor(new PreMergeLOCFileSizeProcessor());
        processor.addProcessor(new PreMergeLOCDiffProcessor());
        processor.addProcessor(new PreMergeASTSizeProcessor());
        processor.addProcessor(new PreMergeASTDiffProcessor());
        processor.addProcessor(new MetricProcessor());
        processor.addProcessor(new AuthorProcessor());
        processor.addProcessor(new MergedInMasterProcessor());
    }

    void generateDiffs(String projectName, List<CommitStatus> statuses, Main.Config config) {
        VisualizationDataGenerator dataGenerator = new VisualizationDataGenerator();
        dataGenerator.setURLFolder(config.urlFolder);
        statuses.stream().filter(this::containsJavaFiles)
                .forEach((status) -> dataGenerator.generateData(projectName, statuses, config.vizFolder));
    }

    boolean containsJavaFiles(CommitStatus status) {
        return getFilesOfInterest(status).stream().anyMatch((file) -> file.endsWith("java"));
    }

    List<String> getFilesOfInterest(CommitStatus status) {
        return status.getModifiedFiles();
    }

    List<CommitStatus> recreateMergesInRepository(String repositoryPath) throws IOException,
            WalkException {
        Repository repository = Git.open(new File(repositoryPath)).getRepository();
        List<RevCommit> mergeCommits = new RepositoryWalker(repository).getMergeCommits();
        InMemoryMerger merger = new InMemoryMerger(repository);

        List<CommitStatus> statuses = mergeCommits.stream().parallel().map((commit) -> merger.recreateMerge(commit))
                .collect(Collectors.toList());
        return statuses;
    }

    void processResultsAndWriteToStream(List<CommitStatus> statuses, OutputStream outputStream) throws IOException {
        outputStream.write((processor.getHeader() + "\n").getBytes());
        statuses.stream().parallel().map(this::processCommitStatus).forEach((r) -> {
            try {
                outputStream.write(r.getBytes());
            } catch (IOException e) {
            }
        });
    }

    public String processCommitStatus(CommitStatus status) {
        String statusResult = getFilesOfInterest(status).stream()
                .filter((file) -> file.endsWith("java"))
                .map((file) -> processor.getData(status, file))
                .collect(Collectors.joining("\n"));
        if (statusResult.equals(""))
            return statusResult;
        else
            return statusResult += "\n";
    }
}