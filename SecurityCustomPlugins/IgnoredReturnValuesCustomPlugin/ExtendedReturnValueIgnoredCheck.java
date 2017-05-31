package com.google.errorprone.bugpatterns;

import static com.google.common.collect.Iterables.getLast;
import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.instanceMethod;
import static com.google.errorprone.matchers.method.MethodMatchers.staticMethod;
import static com.google.errorprone.matchers.Matchers.allOf;
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
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

import com.google.errorprone.bugpatterns.ReturnValueIgnored;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

@AutoService(BugChecker.class)

@BugPattern(
  	name = "ExtendedReturnValueIgnoredCheck",
	category = JDK,
	summary = "Return value ignored. Extended version of ReturnValueIgnored with File" 
                   +" Operations",
        explanation = "Certain library methods do nothing useful if their return value is ignored. Also, this extended"
                   + " version checks for security risk issues where the return value is ignored.",
	severity = ERROR
)

public class ExtendedReturnValueIgnoredCheck extends ReturnValueIgnored
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

  //exact same as parent but since it's private in parent, have to copy it in order to use it... 
  private static Matcher<ExpressionTree> methodReturnsSameTypeAsReceiver() {
    return new Matcher<ExpressionTree>() {
      @Override
      public boolean matches(ExpressionTree expressionTree, VisitorState state) {
        return isSameType(
            ASTHelpers.getReceiverType(expressionTree),
            ASTHelpers.getReturnType(expressionTree),
            state);
      }
    };
   }

    Matcher<ExpressionTree> FILE_MODIFICATION_METHOD = allOf(methodIsDangerFileMethod(fileMethodsToCheck), 
           methodReturnsSameTypeAsReceiver());

    private static Matcher<ExpressionTree> methodIsDangerFileMethod(final Set<String> methodsList)
    {
        return new Matcher<ExpressionTree>() {
             public boolean matches(ExpressionTree expressionTree, VisitorState state) {
                 boolean classUsesFileDangerMethod = false;
                 Matcher<ExpressionTree> tempMatcher;
                 Iterator<String> fileMethodsIterator = fileMethodsToCheck.iterator();
                 String currentMethod;
                 for(int i = 0; i < fileMethodsToCheck.size(); i++)
                 {
                    currentMethod = fileMethodsIterator.next();
                    tempMatcher = instanceMethod().onDescendantOf("java.io.File").named(currentMethod);
                    System.out.println("Checking if " + currentMethod + " is being called");
                    classUsesFileDangerMethod = tempMatcher.matches(expressionTree, state);
                    System.out.println("Is being called: " + classUsesFileDangerMethod);
                    if(classUsesFileDangerMethod)
                    {
                       return true;
                    }
                 }

                 return false;
              }
           };
    }

    public Matcher<? super MethodInvocationTree> specializedMatcher() 
    {
    	return anyOf(super.specializedMatcher(), FILE_MODIFICATION_METHOD);
    }
}
