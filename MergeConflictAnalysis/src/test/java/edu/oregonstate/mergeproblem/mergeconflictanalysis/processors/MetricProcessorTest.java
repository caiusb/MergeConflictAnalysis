package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.junit.Before;
import org.junit.Test;

public class MetricProcessorTest extends ProcessorTest {
	
	private MetricProcessor processor;

	@Before
	public void before() throws Exception {
		super.before();
		processor = new MetricProcessor();
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
	
	@Test
	public void testAddIfStatement() {
		String a = "public class A{public void m(){}}";
		String b = "public class B{public void m(){if(true){}}";
		String results = processor.getResults(a, b);
		assertEquals("0,1", results);
	}
	
	@Test
	public void testAddWhileStatement() {
		String a = "public class A{public void m(){}}";
		String b = "public class B{public void m(){while(true){}}";
		String result = processor.getResults(a, b);
		assertEquals("0,1", result);
	}
	
	@Test
	public void testAddForStatement() {
		String a = "public class B{public void m(){}";
		String b = "public class B{public void m(){for(int i=0; i<10; i++){}}";
		String results = processor.getResults(a, b);
		assertEquals("0,1", results);
	}
	
	@Test
	public void testCaseStatement() {
		String a = "public class B{public void m(){}}";
		String b = "public class B{public void m(){int x = 3; switch(x){case 1: x = 2;}}}";
		String results = processor.getResults(a, b);
		assertEquals("0,1", results);
	}
	
	@Test
	public void testFancyForStatement() {
		String a = "public class B{public void m(){}}";
		String b = "public class B{public void m(){List<String> l = new ArrayList<String>(); for(String s : l){}}}";
		String results = processor.getResults(a, b);
		assertEquals("0,1", results);
	}
}
