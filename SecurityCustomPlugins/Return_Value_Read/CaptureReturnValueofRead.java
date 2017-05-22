import com.google.errorprone.*;

import static com.google.common.collect.Iterables.getLast;
import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.instanceMethod;
import static com.google.errorprone.matchers.method.MethodMatchers.staticMethod;

import com.google.auto.service.AutoService;
import com.google.common.collect.Iterables;
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

/* Check #4: Use an int to capture the return value of methods that read a character or byte
   
   Check for read() function and then check if the return int value is captured or not.
   Severity: High
   Likelihood: Probable
   Remediation Cost: Medium
   
   Goal of Checker:
   1) Find where a the read() is called. 
   2) check if the return value is captured or ignored
*/
@AutoService(BugChecker.class)

@BugPattern(
	name = "CaptureReturnValueofRead",
	category = JDK,
	summary = "Use an int to capture the return value of methods that read a character or byte",
	severity = ERROR
)

public class CaptureReturnValueofRead extends BugChecker implements MethodInvocationTreeMatcher {

//temporarily copying from the example to later refactor into what it's supposed to do...

  Matcher<ExpressionTree> PRINT_METHOD =
      instanceMethod().onDescendantOf(PrintStream.class.getName()).named("print");

  Matcher<ExpressionTree> STRING_FORMAT =
      staticMethod().onClass(String.class.getName()).named("format");

  @Override
  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if (!PRINT_METHOD.matches(tree, state)) {
      return NO_MATCH;
    }
    Symbol base =
        tree.getMethodSelect()
            .accept(
                new TreeScanner<Symbol, Void>() {
                  @Override
                  public Symbol visitIdentifier(IdentifierTree node, Void unused) {
                    return ASTHelpers.getSymbol(node);
                  }

                  @Override
                  public Symbol visitMemberSelect(MemberSelectTree node, Void unused) {
                    return super.visitMemberSelect(node, null);
                  }
                },
                null);
    if (!Objects.equals(base, state.getSymtab().systemType.tsym)) {
      return NO_MATCH;
    }
    ExpressionTree arg = Iterables.getOnlyElement(tree.getArguments());
    if (!STRING_FORMAT.matches(arg, state)) {
      return NO_MATCH;
    }
    List<? extends ExpressionTree> formatArgs = ((MethodInvocationTree) arg).getArguments();
    return describeMatch(
        tree,
        SuggestedFix.builder()
            .replace(
                ((JCTree) tree).getStartPosition(),
                ((JCTree) formatArgs.get(0)).getStartPosition(),
                "System.err.printf(")
            .replace(
                state.getEndPosition((JCTree) getLast(formatArgs)),
                state.getEndPosition((JCTree) tree),
                ")")
            .build());
  }
}
}
