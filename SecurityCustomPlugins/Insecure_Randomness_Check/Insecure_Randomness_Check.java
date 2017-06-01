package com.google.errorprone.bugpatterns;

import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.matchers.Matchers.anyOf;
import static com.google.errorprone.matchers.method.MethodMatchers.staticMethod;

import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.tools.javac.tree.JCTree;


@BugPattern(
  name = "Insecure_Randomness_Check",
  altNames = {"Insecure_Randomness"},
  summary = "Generate numbers with strong randomness using Java Cryptographic Library.",
  category = JDK,
  severity = ERROR
)
public class Insecure_Randomness_Check extends BugChecker implements MethodInvocationTreeMatcher {
  private static final String MESSAGE_BASE = "Insecure Method For Generating Random Numbers: ";

  private static final Matcher<ExpressionTree> RANDOMGEN_NEXTINT_MATCHER =
      staticMethod().onClass("java.util.Random").named("nextInt");

  private Description buildErrorMessage(MethodInvocationTree tree, String explanation) {
    Description.Builder description = buildDescription(tree);
    String message = MESSAGE_BASE + explanation + ".";
    description.setMessage(message);
    return description.build();
  }

  @Override
  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    Description description = checkInvocation(tree, state);


    return description;
  }

  Description checkInvocation(MethodInvocationTree tree, VisitorState state) {
    if (RANDOMGEN_NEXTINT_MATCHER.matches(tree, state)) {
      return buildErrorMessage(tree, "Please Use SecureRandom Instead");
    }

    return Description.NO_MATCH;
  }
}
