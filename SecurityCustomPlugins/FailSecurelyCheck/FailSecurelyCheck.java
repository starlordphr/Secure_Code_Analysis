package com.google.errorprone.bugpatterns;

import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.instanceMethod;
import static com.google.errorprone.util.ASTHelpers.isSameType;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.CatchTreeMatcher;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.Tree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.TryTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;

import com.google.errorprone.bugpatterns.BugChecker.TryTreeMatcher;

@AutoService(BugChecker.class)

@BugPattern(
name = "FailSecurelyCheck",
altNames = {"Fail_Securely"},
summary = "There should be a finally block in all the good programs. This helps your program to fail securely.",
category = JDK,
severity = ERROR
)

public class FailSecurelyCheck extends BugChecker implements TryTreeMatcher{

  @Override
  public Description matchTry(TryTree tree, VisitorState state) {
    if (new FinallyCompletionMatcher<TryTree>().matches(tree, state)) {
      return describeMatch(tree,SuggestedFix.builder().build());
    }
    return Description.NO_MATCH;
  }

  private static class FinallyCompletionMatcher<T extends StatementTree> implements Matcher<T> {
    @Override
    public boolean matches(T tree, VisitorState state) {
      if (tree instanceof TryTree){
        TryTree tryTree = (TryTree) tree;
        if (tryTree.getFinallyBlock() == null) {
          return true;
        }
        return false;
      }
      return false;
    }
  }
}
