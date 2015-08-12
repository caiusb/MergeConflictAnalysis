package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.junit.Before;
import org.junit.Test;

public class CouplingChangeProcessorTest extends ProcessorTest {
	
	private CouplingChangeProcessor processor;

	@Before
	public void before() throws Exception {
		super.before();
		processor = new CouplingChangeProcessor();
	}
	
	@Test
	public void testHeader() {
		assertEquals("COUPLING_CHANGE,CYCLO_CHANGE", processor.getHeader());
	}
	
	@Test
	public void testAddInFieldDeclaration() {
		String a = "public class A{}";
		String b = "public class A{public String a = \"\";}";
		String results = processor.getResults(a, b);
		assertEquals("1,0", results);
	}
	
	@Test
	public void testAddExistingType() {
		String a = "public class A{public String a = \"\";}";
		String b = "public class A{public String a = \"\";public String b = \"\";}";
		String results = processor.getResults(a, b);
		assertEquals("0,0", results);
	}
	
	@Test
	public void testCreateNewObject() {
		String a = "public class A{public Object x = null;}";
		String b = "public class A{public Object x = new String()}";
		String results = processor.getResults(a, b);
		assertEquals("1,0", results);
	}
	
	@Test
	public void testChangeObjectType() {
		String a = "public class A{public Object x = null;}";
		String b = "public class A{public String a = \"\";}";
		String results = processor.getResults(a, b);
		assertEquals("0,0", results);
	}
	
	@Test
	public void testCountEachTypeOnlyOnce() {
		String a = "public class A{public Object x = null;}";
		String b = "public class A{public Object x = null; public String a; public String b;}";
		String results = processor.getResults(a, b);
		assertEquals("1,0", results);
	}
	
	@Test
	public void testDontCountPrimitiveTypes() {
		String a = "public class A{}";
		String b = "public class B{public int x = 1;}";
		String results = processor.getResults(a, b);
		assertEquals("0,0", results);
	}
	
	@Test
	public void testDeleteDeclaration() {
		String a = "public class A{public String a = \"\";}";
		String b = "public class A{}";
		String results = processor.getResults(a, b);
		assertEquals("1,0", results);
	}
	
	@Test
	public void testDeleteDeclarationWithExistingType() {
		String a = "public class A{public String a = \"\";}";
		String b = "public class A{public String a = \"\";public String b = \"\";}";
		String results = processor.getResults(b, a);
		assertEquals("0,0", results);
	}
	
	@Test
	public void testDeleteCreation() {
		String a = "public class A{public Object x = null;}";
		String b = "public class A{public Object x = new String()}";
		String results = processor.getResults(b, a);
		assertEquals("1,0", results);
	}
	
	@Test
	public void testFullyQualifiedNames() {
		String a = "public class A{}";
		String b = "public class A{public String x; public java.lang.String y;}";
		String results = processor.getResults(a, b);
		assertEquals("1,0", results);
	}
}
