package com.google.errorprone.bugpatterns;

import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.matchers.Matchers.anyOf;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.instanceMethod;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.tools.javac.tree.JCTree;

import com.google.errorprone.fixes.SuggestedFix;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;

import java.io.FileInputStream;
import java.io.FileReader;

import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.code.Type;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;

import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.code.Symbol;
import com.sun.source.tree.BinaryTree;

@AutoService(BugChecker.class)

@BugPattern(
  name = "CaptureReturnValueofRead",
  altNames = {"CaptureReturnValue"},
  summary = "Use an int to capture the return value of methods that read a character or byte",
  category = JDK,
  severity = ERROR
)
public class CaptureReturnValueofRead extends BugChecker implements MethodInvocationTreeMatcher {

  private Matcher<ExpressionTree> CAPTURE_READ_MATCHER =
      anyOf(
      instanceMethod().onExactClass(FileInputStream.class.getName()).named("read"),
      instanceMethod().onExactClass(FileReader.class.getName()).named("read"));

  @Override
  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    String arg1;
    if(!CAPTURE_READ_MATCHER.matches(tree,state))
    {
        return NO_MATCH;
    }

    arg1 = ((JCFieldAccess) tree.getMethodSelect()).getExpression().toString();

    Type returnType =
        ASTHelpers.getReturnType(((JCMethodInvocation) tree).getMethodSelect());
    Tree parent = state.getPath().getParentPath().getParentPath().getLeaf();
    String expressionString = parent.toString();

    if (expressionString.contains("(byte)") || expressionString.contains("(char)"))
    {
      return describeMatch(tree,SuggestedFix.builder().build());
    }

    return NO_MATCH;
  }
}
