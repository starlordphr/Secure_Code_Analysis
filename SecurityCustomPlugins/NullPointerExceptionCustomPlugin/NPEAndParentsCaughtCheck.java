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
import com.sun.source.tree.CatchTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;

import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;

@AutoService(BugChecker.class)

@BugPattern(
  	name = "NPEAndParentsCaughtCheck",
	category = JDK,
	summary = "Security Risk! Null Pointer Exception, Runtime Exception, and Exception are too generic and can be used "
                   + "by an attacker to instigate a control flow that is not secure!",
	severity = ERROR
)

public class NPEAndParentsCaughtCheck extends BugChecker implements BugChecker.CatchTreeMatcher
{
   @Override
   public Description matchCatch(CatchTree tree, VisitorState state)
   {
      Type exceptionType = ((JCTree) tree.getParameter().getType()).type;
      boolean isRuntimeException = isSameType(exceptionType, state.getSymtab().runtimeExceptionType, state);
      boolean isException = isSameType(exceptionType, state.getSymtab().exceptionType, state);

      String nPEString = "NullPointerException";
      boolean isNPE = tree.getParameter().getType().toString().equals(nPEString);
      if(isRuntimeException || isNPE || isException)
      {
         return describeMatch(tree,SuggestedFix.builder().build());
      }
      return NO_MATCH;
   }

}
