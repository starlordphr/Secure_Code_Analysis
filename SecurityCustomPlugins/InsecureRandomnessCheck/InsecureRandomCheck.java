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

import java.util.Random;

@AutoService(BugChecker.class)

@BugPattern(
  name = "InsecureRandomCheck",
  altNames = {"Insecure_Randomness"},
  summary = "Use the java.security.SecureRandom class to produce high-quality random numbers.\n -- Example Usage: SecureRandom number = SecureRandom.getInstance('SHA1PRNG');\n -- Example Usage: number.nextInt(21)",
  category = JDK,
  severity = ERROR
)
public class InsecureRandomCheck extends BugChecker implements MethodInvocationTreeMatcher {

  private Matcher<ExpressionTree> RANDOMGEN_NEXTINT_MATCHER =
      instanceMethod().onExactClass(Random.class.getName()).named("nextInt");

  @Override
  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if(!RANDOMGEN_NEXTINT_MATCHER.matches(tree,state))
    {
        return NO_MATCH;
    }

    return describeMatch(tree,SuggestedFix.builder().build());
  }
}
