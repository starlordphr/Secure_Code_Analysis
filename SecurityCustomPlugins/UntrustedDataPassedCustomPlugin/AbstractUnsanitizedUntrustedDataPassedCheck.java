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
public abstract class UnsanitizedUntrustedDataPassedCheck extends BugChecker implements MethodInvocationTreeMatcher {

  //TO BE OVERWRITTEN
  //is the method that establishes the "connection" to pass the string to (e.g. in SQL it's connection, in Runtime exec, it's
  //Runtime.exec.
  Matcher<ExpressionTree> LANGUAGE_METHOD;

  //since all cases pass if an if check on the parameters was performed => placing it in the parent
  //will also take into account switch statements too.
  private boolean hasIfCheck(ExpressionTree arg)
  {
     return true;
  }

  //TO BE OVERWRITTEN
  //returns the suggested fix for the error
  abstract SuggestedFix getCorrection(List<? extends ExpressionTree> fixArgs);

  //TO BE OVERWRITTEN
  //returns if there are other conditions the determine if the code passes or not. Should default be true and children
  //write conditions on when it is false (okay)
  abstract boolean isViolating(Symbol callerClassSymbol, MethodInvocationTree  tree, VisitorState state); 

  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if (!LANGUAGE_METHOD.matches(tree, state)) {
      return NO_MATCH;
    }

    ExpressionTree arg = Iterables.getOnlyElement(tree.getArguments());

    if(hasIfCheck(arg) || !isViolating(tree,state))
    {
      return NO_MATCH;
    }

    //violation found!

    List<? extends ExpressionTree> formatArgs = ((MethodInvocationTree) arg).getArguments();

    //return the description to notify suggested fix.
    return describeMatch(tree,getCorrection(formatArgs));
  }
}
}
