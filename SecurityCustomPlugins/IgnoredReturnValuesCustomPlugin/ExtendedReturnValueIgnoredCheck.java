package com.google.errorprone.bugpatterns;

import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.instanceMethod;
import static com.google.errorprone.matchers.Matchers.anyOf;
import static com.google.errorprone.util.ASTHelpers.isSameType;

import com.google.auto.service.AutoService;
import com.google.common.collect.Iterables;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import java.util.List;
import java.util.Objects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.io.File;
import java.util.HashSet;

@AutoService(BugChecker.class)

@BugPattern(
  	name = "ExtendedReturnValueIgnoredCheck",
	category = JDK,
	summary = "Return value ignored. Extended version of ReturnValueIgnored with File" 
                   +" Operations",
        explanation = "This extension to ReturnValueIgnored checks for security risk issues where the return value is" +                     " ignored.",
	severity = ERROR
)

public class ExtendedReturnValueIgnoredCheck extends AbstractReturnValueIgnored
{
    //original did not check boolean values returned from File. Methods to be sensitive to:
    //delete, createNewFile(), setReadOnly, setReadable, setWritable, setExecutable
    //refer to https://docs.oracle.com/javase/7/docs/api/java/io/File.html

    private final static Set<String> fileMethodsToCheck =
      new HashSet<>(
          Arrays.asList(
              "delete",
              "createNewFile",
              "setReadOnly",
              "setReadable",
              "setWritable",
              "setExecutable",
              "mkdir",
              "renameTo",
              "setLastModified"));

    private static Matcher<ExpressionTree> FILE_MODIFICATION_METHOD = anyOf(getFileModificationSet(fileMethodsToCheck));

    private static Set<Matcher<ExpressionTree>> getFileModificationSet(final Set<String> methodsList)
    {
        Set<Matcher<ExpressionTree>> setToReturn = new HashSet<Matcher<ExpressionTree>>();
        Iterator<String> fileMethodsIterator = methodsList.iterator();
        while(fileMethodsIterator.hasNext())
        {
           String methodName = fileMethodsIterator.next();
           setToReturn.add(instanceMethod().onDescendantOf(File.class.getName()).named(methodName));
        }
        
        return setToReturn; 
    }

    public Matcher<? super MethodInvocationTree> specializedMatcher() 
    {
        return FILE_MODIFICATION_METHOD;
    }
}
