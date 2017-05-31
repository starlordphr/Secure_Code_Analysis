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
public class RuntimeExecUnsanitizedDataCheckJUnitTest {

	private CompilationTestHelper compilationHelper;

	  @Before
	  public void setup() {
	    compilationHelper = CompilationTestHelper.newInstance(RuntimeExecUnsanitizedDataCheck.class, getClass());
	  }
	  
	  @Test
	  public void runtimeExecUnsanitizedDataPositiveCases() {
	    compilationHelper.addSourceFile("RuntimeExecUnsanitizedDataCheckPositiveCases.java").doTest();
	  }

	  @Test
	  public void runtimeExecUnsanitizedDataNegativeCases()  {
	    compilationHelper.addSourceFile("RuntimeExecUnsanitizedDataCheckPositiveCases.java").doTest();
	  }
}
