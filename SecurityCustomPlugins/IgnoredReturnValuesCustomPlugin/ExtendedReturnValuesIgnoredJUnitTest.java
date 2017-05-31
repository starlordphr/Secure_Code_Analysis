package com.google.errorprone.bugpatterns;

import com.google.errorprone.CompilationTestHelper;
import com.google.testing.testsize.MediumTest;
import com.google.testing.testsize.MediumTestAttribute;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@MediumTest(MediumTestAttribute.FILE)
public class ExtendedReturnValuesIgnoredJUnitTest {

	private CompilationTestHelper compilationHelper;

	  @Before
	  public void setup() {
	    compilationHelper = CompilationTestHelper.newInstance(ExtendedReturnValuesIgnoredCheck.class, getClass());
	  }

	  @Test
	  public void extendedReturnValuesIgnoredPositiveCases() {
	    compilationHelper.addSourceFile("ExtendedReturnValuesIgnoredPositiveCases.java").doTest();
	  }

	  @Test
	  public void extendedReturnValuesIgnoredNegativeCases()  {
	    compilationHelper.addSourceFile("ExtendedReturnValuesIgnoredNegativeCases.java").doTest();
	  }

}
