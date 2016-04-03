package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.InMemoryMerger;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

public class ASTSizeProcessorTest extends ProcessorTest {
	
	private ASTSizeProcessor processor;
	
	@Before
	public void before() throws Exception {
		super.before();
		processor = new ASTSizeProcessor();
	}

	@Test
	public void testHeader() {
		String header = processor.getHeader();
		assertEquals("COMBINED_AST_SIZE_A,COMBINED_AST_SIZE_B,AST_SIZE_SOLVED", header);
	}
	
	@Test
	public void testData() throws Exception {
		CommitStatus status = generateCommitStatus();
		String data = processor.getData(status, status.getListOfConflictingFiles().get(0));
		assertEquals("4,4,4", data);
	}

	@Test
	public void testNonParsableCombinedFile() throws Exception {
		add("A.java", "public class A{\npublic void m(){\n}\n}");
		branch("branch");
		add("A.java", "public class A{\npublic int m(){\nreturn 0;\n}\n}");
		checkout("master");
		add("A.java", "public class {\npublic double m(){\nif(true){\nreturn 1;\n} else {\n return 3;\n}\n}\n}");
		MergeResult result = merge("branch");
		assertFalse(result.getMergeStatus().isSuccessful());
		RevCommit resolved = add("A.java", "public class {\npublic double m(){\nif(true){\nreturn 0;\n} else {\n return 3;\n}\n}\n}");
		CommitStatus status = new InMemoryMerger(repository).recreateMerge(resolved);
		String data = processor.getData(status, status.getListOfConflictingFiles().get(0));
		assertEquals("NA,11,NA", data);
	}

}
