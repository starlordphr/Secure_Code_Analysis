package com.google.errorprone.bugpatterns;

import com.google.errorprone.*;

import static com.google.common.collect.Iterables.getLast;
import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.instanceMethod;

import com.google.errorprone.bugpatterns.BugChecker.MethodTreeMatcher;
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
public abstract class AbstractUnsanitizedUntrustedDataPassedCheck extends BugChecker implements MethodTreeMatcher {

  //TO BE OVERWRITTEN
  //is the method that establishes the "connection" to pass the string to (e.g. in SQL it's connection, in Runtime exec, it's
  //Runtime.exec.
  abstract Matcher<ExpressionTree> getLanguageMethod();

  //TO BE OVERWRITTEN
  //returns if there are other conditions the determine if the code passes or not. Should default be true and children
  //write conditions on when it is false (okay)
  abstract boolean isViolating(MethodTree  tree, VisitorState state); 

  public Description matchMethod(MethodTree tree, VisitorState state) {
    if (tree.getBody() == null || tree.getBody().getStatements().isEmpty()) {
      return NO_MATCH;
    }

    //get variables involved in any if tree in the method... can't figure out the tree visitor yet...
    ArrayList<IdentifierTree> variablesCoveredbyIf = new ArrayList<IdentifierTree>();//empty for now...
    
    //get variables involved in any switch tree in the method
    ArrayList<IdentifierTree> variablesCoveredbyCase = new ArrayList<IdentifierTree>();//empty for now...

    //retrieve where the danger methods are called
    List<? extends StatementTree> statements = tree.getBody().getStatements();
    ArrayList<MethodInvocationTree> dangerCalled = new ArrayList<MethodInvocationTree>();
    for(int i = 0; i < statements.size(); i++)
    {
       if(statements.get(i) instanceof ExpressionStatementTree)
       {
          ExpressionTree possibleTree = ((ExpressionStatementTree) statements.get(i)).getExpression();
          if(possibleTree instanceof MethodInvocationTree)
          {
             if(getLanguageMethod().matches((MethodInvocationTree) possibleTree, state))
             {
                  dangerCalled.add((MethodInvocationTree) possibleTree);
             }
          }
       }
    }

    //dangerous trusted boundary not called
    if(dangerCalled.isEmpty())
    {
        return NO_MATCH;
    }

    ArrayList<IdentifierTree> variablesCalledByDanger = new ArrayList<IdentifierTree>();
    for(int i = 0; i < dangerCalled.size(); i++)
    {
        List<? extends ExpressionTree> argsList = dangerCalled.get(i).getArguments();
        for(int j = 0; j < argsList.size(); j++)
        {
            ExpressionTree currentArgument = argsList.get(j);
            variablesCalledByDanger.addAll(parseVariables(currentArgument));
        }
    }

    //dangerous methods are being called w/o any variables being passed in so whew!
    if(variablesCalledByDanger.isEmpty())
    {
       return NO_MATCH;
    }

    boolean ifPass = false;


    if(!isViolating(tree,state))
    {
      return NO_MATCH;
    }

    return describeMatch(tree, SuggestedFix.builder().build());
  }

public static ArrayList<IdentifierTree> parseVariables(ExpressionTree tree)
    {
       ArrayList<IdentifierTree> listToReturn = new ArrayList<IdentifierTree>();
       if(tree instanceof MethodInvocationTree)
        {
            List<? extends Tree> argumentList = ((MethodInvocationTree) tree).getArguments();
            for(int i = 0; i < argumentList.size(); i++)
            {
                listToReturn.addAll(parseVariables((ExpressionTree) argumentList.get(i)));
            }
        }
        else if(tree instanceof BinaryTree)
        {
            ExpressionTree left = ((BinaryTree) tree).getLeftOperand();
            listToReturn = parseVariables(left);

            ExpressionTree right = ((BinaryTree) tree).getRightOperand();
            ArrayList<IdentifierTree> rightVariables = parseVariables(right);
           
            for(int i = 0; i < rightVariables.size(); i++)
            {
               listToReturn.add(rightVariables.get(i));   
            }
        }
        else if(tree instanceof IdentifierTree)
        {
            listToReturn.add((IdentifierTree) tree);
        }

        return listToReturn;
    }

  class IfTreeAccumulator extends TreeScanner{
     ArrayList<IdentifierTree> visitIf(IfTree tree, Void v)
     {
        ExpressionTree condition = tree.getCondition();
        return parseVariables(condition);
     }

    
    ArrayList<IdentifierTree> reduce(ArrayList<IdentifierTree> prev, ArrayList<IdentifierTree> next)
    {
        ArrayList<IdentifierTree> itemsToRemoveFromNext = new ArrayList<IdentifierTree>();
        for(int i = 0; i < prev.size(); i++)
        {
           for(int j = 0; j < next.size(); j++)
           {
               if(prev.get(i).equals(next.get(j)))
               {
                  itemsToRemoveFromNext.add(next.get(j));
               }
           }
        }

       next.removeAll(itemsToRemoveFromNext);

       for(int i = 0; i < next.size(); i++)
       {
           prev.add(next.get(i));
       }

        return prev;
    }
  }
}
