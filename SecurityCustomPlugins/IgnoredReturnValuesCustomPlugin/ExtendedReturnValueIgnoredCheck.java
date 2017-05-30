package com.google.errorprone.bugpatterns;

import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.matchers.Matchers.allOf;
import static com.google.errorprone.matchers.Matchers.anyOf;
import static com.google.errorprone.matchers.method.MethodMatchers.instanceMethod;
import static com.google.errorprone.util.ASTHelpers.isSameType;

import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;

import com.google.errorprone.bugpatterns.ReturnValueIgnored;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@AutoService(BugChecker.class)

@BugChecker(
  	name = "ExtendedReturnValueIgnoredCheck",
	category = JDK,
	summary = "Return value of this method must be used. Extended version of ReturnValueIgnored with File" 
                   +" Operations",
	severity = ERROR
)

public class ExtendedReturnValueIgnoredCheck extends ReturnValueIgnored
{
    //original did not check boolean values returned from File. Methods to be sensitive to:
    //delete, createNewFile(), setReadOnly, setReadable, setWritable, setExecutable
    //refer to https://docs.oracle.com/javase/7/docs/api/java/io/File.html

    private final Set<String> fileMethodsToCheck =
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
 

    Matcher<ExpressionTree> FILE_MODIFICATION_METHOD = allOf(methodIsDangerFileMethod(fielMethodsToCheck), 
           methodReturnsSameTypeAsReceiver());

    private static Matcher<ExpressionTree> methodIsDangerFileMethod(final Set<String> methodsList)
    {
        return new Matcher<ExpressionTree>() {
             public boolean matches(ExpressionTree expressionTree, VisitorState state) {
                 boolean classUsesFileDangerMethod = false;
                 Matcher<ExpressionTree> tempMatcher;
                 for(int i = 0; i < fileMethodsToCheck.size(); i++)
                 {
                    tempMatcher = instanceMethod().onDescendantOf("java.io.File").named(fileMethodsToCheck.get(i));
                    System.out.println("Checking if " + fileMethodsToCheck.get(i) + " is being called");
                    classUsesFileDangerMethod = tempMatcher.matches(expressionTree, state);
                    System.out.println("Is being called: " + classUsesFileDangerMethod);
                    if(classUsesFileDangerMethod)
                    {
                       return true;
                    }
                 }
                 return classUsesFileDangerMethod;
              }
           };
    }

    public Matcher<? super MethodInvocationTree> specializedMatcher() 
    {
    	return anyOf(RETURNS_SAME_TYPE, FUNCTIONAL_METHOD, STREAM_METHOD, FILE_MODIFICATION_METHOD);
    }
}
