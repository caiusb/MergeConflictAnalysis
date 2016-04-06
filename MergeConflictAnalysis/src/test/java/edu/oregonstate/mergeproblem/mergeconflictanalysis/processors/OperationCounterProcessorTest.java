package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import org.junit.Test;

public class OperationCounterProcessorTest extends ProcessorTest {
	
	@Test
	public void testHeader() {
		String header = new OperationCounterProcessor().getHeader();
		assertEquals("NO_ADD,NO_UPDATE,NO_DELETE", header);
	}

	@Test
	public void testModifyOperation() throws Exception {
		String a = "public class A{}";
		String b = "public class B{}";
		CommitStatus commitStatus = createCommitStatus(a, b);
		
		String data = new OperationCounterProcessor().getData(commitStatus, FILE_NAME);
		assertEquals("0,1,0", data);
	}

	@Test
	public void testDeleteOperation() throws Exception {
		String a = "public class A{public void m(){}}";
		String b = "public class A{}";
		CommitStatus commitStatus = createCommitStatus(a, b);

		String data = new OperationCounterProcessor().getData(commitStatus, FILE_NAME);
		assertEquals("0,0,5", data);
	}
	
	@Test
	public void testAddOperation() throws Exception {
		String a = "public class A{}";
		String b = "public class A{public void m(){}}";
		CommitStatus commitStatus = createCommitStatus(a, b);

		String data = new OperationCounterProcessor().getData(commitStatus, FILE_NAME);
		assertEquals("5,0,0", data);
	}
}
