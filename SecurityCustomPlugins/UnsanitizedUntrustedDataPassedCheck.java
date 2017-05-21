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

/* Check #7: Unsanitized Untrusted Data Being Passed Through a Trust Boundary Check
   (shortened the name to make it easier)
   
   Checks for where a separate language (e.g. SQL or XML) has a string being constructed
   that uses variables whose values are not yet validated.
   
   Goal of Checker:
   1) Find where a String is being built with variables. 
       a. Pinpoint language being used
       b. Check if it is a valid concern (varies per language if valid concern)
          e.g. SQL requires a Connection or Statement. XML requires a stream.
   2) See if it violates the check based on the Language
*/
@AutoService(BugChecker.class)

@BugPattern(
	name = "UnsanitizedUntrustedDataPassedCheck",
	category = JDK,
	summary = "Untrusted Data not checked for constraints before being passed into string construction",
	severity = ERROR
)

public class UnsanitizedUntrustedDataPassedCheck extends BugChecker implements MethodInvocationTreeMatcher {

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
