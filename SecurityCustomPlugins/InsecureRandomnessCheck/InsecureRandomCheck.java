package com.google.errorprone.bugpatterns;

import com.google.errorprone.BugPattern;
import com.google.errorprone.BugPattern.Category;
import com.google.errorprone.BugPattern.SeverityLevel;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker.BinaryTreeMatcher;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree.Kind;

import com.google.auto.service.AutoService;

import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.instanceMethod;
import static com.google.errorprone.util.ASTHelpers.isSameType;

@AutoService(BugChecker.class)

@BugPattern(
  name = "InsecureRandomCheck",
  summary = "Don't use Random.nextInt(int).  Use SecureRandom.getInstance(...) Instead",
  explanation =
      "`Random.nextInt() % n` has \n\n"
          + "* a 1/n chance of being 0\n"
          + "* a 1/2n chance of being each number from `1` to `n-1` inclusive\n"
          + "* a 1/2n chance of being each number from `-1` to `-(n-1)` inclusive\n\n"
          + "Many users expect a uniformly distributed random integer between `0` and `n-1` "
          + "inclusive, but you must use random.nextInt(n) to get that behavior.  If the original "
          + "behavior is truly desired, use `(random.nextBoolean() ? 1 : -1) * random.nextInt(n)`.",
  severity = SeverityLevel.ERROR,
  category = Category.JDK
)
public class InsecureRandomCheck extends BugChecker implements BinaryTreeMatcher {

  private static final Matcher<ExpressionTree> RANDOM_NEXT_INT =
      Matchers.instanceMethod().onDescendantOf("java.util.Random").withSignature("nextInt()");

  @Override
  public Description matchBinary(BinaryTree tree, VisitorState state) {
    System.out.println("sdsdsdsdsd");
    if (RANDOM_NEXT_INT.matches(tree, state)) {
      return describeMatch(tree,SuggestedFix.builder().build());
    }
    return Description.NO_MATCH;
  }
}
