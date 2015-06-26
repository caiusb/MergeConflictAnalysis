package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class OperationCounterProcessorTest extends ProcessorTest {
	
	@Test
	public void testHeader() {
		String header = new OperationCounterProcessor().getHeader();
		assertEquals("NO_ADD,NO_UPDATE,NO_DELETE", header);
	}

	@Test
	public void testModifyOperation() {
		String a = "public class A{}";
		String b = "public class B{}";
		CommitStatus commitStatus = crateCommitStatus(a, b);
		
		String data = new OperationCounterProcessor().getData(commitStatus, FILE_NAME);
		assertEquals("0,1,0", data);
	}

	@Test
	public void tesDeleteOperation() {
		String a = "public class A{public void m(){}}";
		String b = "public class A{}";
		CommitStatus commitStatus = crateCommitStatus(a, b);

		String data = new OperationCounterProcessor().getData(commitStatus, FILE_NAME);
		assertEquals("0,0,5", data);
	}
	
	@Test
	public void testAddOperation() {
		String a = "public class A{}";
		String b = "public class A{public void m(){}}";
		CommitStatus commitStatus = crateCommitStatus(a, b);

		String data = new OperationCounterProcessor().getData(commitStatus, FILE_NAME);
		assertEquals("5,0,0", data);
	}
}
