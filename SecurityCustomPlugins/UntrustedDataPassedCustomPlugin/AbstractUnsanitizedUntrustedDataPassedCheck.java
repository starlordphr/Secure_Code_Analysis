package com.google.errorprone.bugpatterns;

import com.google.errorprone.*;

import static com.google.common.collect.Iterables.getLast;
import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.instanceMethod;

import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.sun.source.tree.VariableTree;
import com.google.auto.service.AutoService;
import com.google.common.collect.Iterables;
import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.VisitorState;

import javax.lang.model.element.Name;

import java.util.List;
import java.util.ArrayList;

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
public abstract class AbstractUnsanitizedUntrustedDataPassedCheck extends BugChecker implements MethodInvocationTreeMatcher {

  //TO BE OVERWRITTEN
  //is the method that establishes the "connection" to pass the string to (e.g. in SQL it's connection, in Runtime exec, it's
  //Runtime.exec.
  Matcher<ExpressionTree> LANGUAGE_METHOD;
  private VariableTree variableWithNoCaseStatement;
  private VariableTree variableWithNoIfStatement;

  private boolean hasCaseCheck(MethodInvocationTree tree, ArrayList<VariableTree> variables)
  {
      //can't get to parent... and having trouble with tree scanner
      //Tree parentTree = tree.parent();
      Tree parentTree = tree;
      TreeScanner tscanner = new TreeScanner<VariableTree, Void>(){
           public Name visitSwitch(SwitchTree node, Void unused)
           {
              return ((IdentifierTree) node.getExpression()).getName();
           }
           };
      for(int i = 0; i < variables.size(); i++)
      {
          if(!parentTree.accept(tscanner).equals(variables.get(i).getName()))
          {
              variableWithNoCaseStatement = variables.get(i);
              return false;
          }
      }
      return true;
  }

  private boolean hasIfCheck(MethodInvocationTree tree, ArrayList<VariableTree> variables)
  {
     return true;
  }

  //TO BE OVERWRITTEN
  //returns if there are other conditions the determine if the code passes or not. Should default be true and children
  //write conditions on when it is false (okay)
  abstract boolean isViolating(MethodInvocationTree  tree, VisitorState state); 

  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if (!LANGUAGE_METHOD.matches(tree, state)) {
      return NO_MATCH;
    }

    //store all the variables that were used in the string part into an arraylist for later verification
    ArrayList<VariableTree> variablesInMethodList = new ArrayList<VariableTree>(); 
    List<? extends Tree> arguments = tree.getArguments();
    for(int i = 0; i < arguments.size(); i++)
    {
        if(arguments.get(i).getKind().equals(Tree.Kind.VARIABLE))
        {
            variablesInMethodList.add(((VariableTree) arguments.get(i)));
        }
    }

    boolean casePass = hasCaseCheck(tree, variablesInMethodList);
    boolean ifPass = hasIfCheck(tree, variablesInMethodList);

    if(casePass || ifPass || !isViolating(tree,state))
    {
      return NO_MATCH;
    }

    //return describeMatch(tree, Description.builder(variableWithNoCaseStatement,"Variable is not validated before use" ,null, 
    //       BugPattern.SeverityLevel.ERROR, variableWithNoCaseStatement.getName() + " is not validated before use").build());
    return describeMatch(tree, SuggestedFix.builder().build());
  }
}
