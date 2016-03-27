package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import org.eclipse.jgit.api.Git;
import org.gitective.tests.GitTestCase;
import org.junit.Test;

import java.util.HashMap;

public class ProjectNameProcessorTest extends GitTestCase {

    @Test
    public void testName() throws Exception {
        ProjectNameProcessor processor = new ProjectNameProcessor();
        assertEquals("PROJECT", processor.getHeader());
        CommitStatus status = new CommitStatus(Git.open(testRepo).getRepository(), "", new HashMap<>(), 3);
        String expected = testRepo.getParentFile().getName();
        assertEquals(expected, processor.getData(status, ""));
    }
}
