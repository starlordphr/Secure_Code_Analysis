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
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.VisitorState;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;

import javax.lang.model.element.Name;
import java.util.Iterator;
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
  abstract Matcher<ExpressionTree> getLanguageMethod();

  //TO BE OVERWRITTEN
  //returns if there are other conditions the determine if the code passes or not. Should default be true and children
  //write conditions on when it is false (okay)
  abstract boolean isViolating(MethodInvocationTree  tree, VisitorState state); 

  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if (!getLanguageMethod().matches(tree,state)) {
      return NO_MATCH;
    }

    List<? extends ExpressionTree> args = tree.getArguments();
    //since this is for a language connection check => arguments should always be string type

    //if a single literal => not a match. (no variables passed!)
    if(args.size() == 1)
    {
       //look for assignment tree if identifier
       ExpressionTree onlyArgument = args.get(0);
       if(onlyArgument instanceof LiteralTree)
       {
          return NO_MATCH;
       }
    }

    ArrayList<IdentifierTree> identifiersToFindIfSwitchFor = new ArrayList<IdentifierTree>();
    ArrayList<ExpressionTree> expressionsToValidate = new ArrayList<ExpressionTree>();
    Iterator argumentIterator = args.iterator();

    

    //check for the assignments of the variables being fed into the language method...
    while(argumentIterator.hasNext())
    {
        ExpressionTree currArg = (ExpressionTree) argumentIterator.next();
        if(currArg.getKind().equals(Tree.Kind.IDENTIFIER))
        {
           identifiersToFindIfSwitchFor.add((IdentifierTree) currArg);
        }
    }

    //loop over parent block to find the respective assignment statements and add to identifiers to find if switch for
    TreePath parentPath = state.getPath().getParentPath();
    BlockTree parentBlock = (BlockTree) parentPath.iterator().next();
    
    List<? extends StatementTree> statementsOfParent = parentBlock.getStatements();
    ArrayList<VariableTree> startingValuesToCheck = new ArrayList<VariableTree>();
    ArrayList<ExpressionTree> furtherInvestigationExpressions = new ArrayList<ExpressionTree>();
    ArrayList<IfTree> parentIfTrees = new ArrayList<IfTree>();
    ArrayList<SwitchTree> parentSwitchTrees = new ArrayList<SwitchTree>();
    for(int i = 0; i < statementsOfParent; i++)
    {
         StatementTree currStatement = statementsOfParent.get(i);
         if(currStatement instanceof VariableTree)
         {
            //modifiers type name initializer ;
            Name varName = ((VariableTree) currStatement).getName();
            for(int j = 0; j < identifiersToFindIfSwitchFor.size(); j++)
            {
               if(varName.equals(identifiersToFindIfSwitchFor.get(j).getName()))
               {
                   startingValuesToCheck.add((VariableTree) currStatement);
                   j = identifiersToFindIfSwitchFor.size();
               }
            }
         }
         else if(currStatement instanceof IfTree)
         {
            //grab the if trees for later use
            parentIfTrees.add((IfTree) currStatement);
         }
         else if(currStatement instanceof SwitchTree)
         {
            //grab the switch trees for later use
            parentSwitchTrees.add((SwitchTree) currStatement);
         }
         else if(currStatement instanceof ExpressionStatementTree)
         {
             //grabs expressions of assignment (x = ...) or CompoundAssignment (String x += ...)
             ExpressionTree exp = ((ExpressionStatementTree) currStatement).getExpression();
             if(exp instanceof AssignmentTree)
             {
                 Name varName = ((AssignmentTree) exp).getVariable().getName();
                 for(int j = 0; j < identifiersToFindIfSwitchFor.size(); j++)
                  {
                    if(varName.equals(identifiersToFindIfSwitchFor.get(j).getName()))
                    {
                       furtherInvestigationExpressions.add(exp);
                    }
                  }
             }
             else if(exp instanceof CompoundAssignmentTree)
             {
                  Name varName = ((CompoundAssignmentTree) exp).getVariable().getName();
                  for(int j = 0; j < identifiersToFindIfSwitchFor.size(); j++)
                  {
                    if(varName.equals(identifiersToFindIfSwitchFor.get(j).getName()))
                    {
                       furtherInvestigationExpressions.add(exp);
                    }
                  }
             }
         }
    }

    //update identifier list with easy to grab identifiers
    for(int i = 0; i < expressionsToValidate.size(); i++)
    {
        ExpressionTree currExpression = expressionsToValidate.get(i);
        //look for the identifier tree in the expression
        TreeScanner<IdentifierTree, Void> identifierScanner = new TreeScanner<IdentifierTree, Void>();
        IdentifierTree idents = currExpression.accept(identifierScanner, null);
        identifiersToFindIfSwitchFor.add(idents);
        boolean keepLooking = true;
        while(keepLooking)
        {
           IdentifierTree nextIdent = currExpression.accept(identifierScanner, null);
           if(nextIdent.equals(idents))
           {
               keepLooking = false;
           }
           else
           {
             identifiersToFindIfSwitchFor.add(nextIdent);
           }
        }

    }

    //find if statements (remove from identifiers to find if switch for)

    //find switch setters (remove from identifiers to find if switch for)

    if(identifiersToFindIfSwitchFor.isEmpty())
    {
        return NO_MATCH;
    }

    if(!isViolating(tree,state))
    {
      return NO_MATCH;
    }

    return describeMatch(tree, SuggestedFix.builder().build());
  }

}
