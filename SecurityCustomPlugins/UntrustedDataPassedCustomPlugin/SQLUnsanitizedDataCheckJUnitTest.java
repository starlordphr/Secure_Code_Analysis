package com.google.errorprone.bugpatterns;

import com.google.errorprone.CompilationTestHelper;
import com.google.testing.testsize.MediumTest;
import com.google.testing.testsize.MediumTestAttribute;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@MediumTest(MediumTestAttribute.FILE)
public class SQLUnsanitizedDataCheckJUnitTest {

	private CompilationTestHelper compilationHelper;

	  @Before
	  public void setup() {
	    compilationHelper = CompilationTestHelper.newInstance(SQLUnsanitizedDataCheck.class, getClass());
	  }
	  
	  @Test
	  public void sqlUnsanitizedDataCheckPositiveCases() {
	    compilationHelper.addSourceFile("SQLUnsanitizedDataCheckPositiveCases.java").doTest();
	  }

	  @Test
	  public void sqlUnsanitizedDataCheckNegativeCases()  {
	    compilationHelper.addSourceFile("SQLUnsanitizedDataCheckNegativeCases.java").doTest();
	  }
}
