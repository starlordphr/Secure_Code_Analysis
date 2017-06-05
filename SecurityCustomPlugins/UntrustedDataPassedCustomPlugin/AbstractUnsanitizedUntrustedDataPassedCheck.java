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
        else if(currArg instanceof BinaryTree)
        {
           ArrayList<IdentifierTree> identifiersInParm = parseBinaryTreeForIdentifiers((BinaryTree) currArg);
           for(int j = 0; j < identifiersInParm.size(); j++)
           {
              identifiersToFindIfSwitchFor.add(identifiersInParm.get(j));
           }
        }
    }

    //loop over parent block to find the respective assignment statements and add to identifiers to find if switch for
    TreePath parentPath = state.getPath().getParentPath().getParentPath();
    Iterator parentIterator = parentPath.iterator();
    if(!parentIterator.hasNext())
    {
       return NO_MATCH;
    }
    Tree parentCaller = (Tree) parentIterator.next();
    //want to only call those not in an ifstatement, try & catch, etc. too complicated => why it's a warning message
    if(!(parentCaller instanceof BlockTree))
    {
       return NO_MATCH; //for now.. not too sure how I want the final decision to go
    }
    BlockTree parentBlock = (BlockTree) parentCaller;
    //note: parent of a methodInvocation is a StatementTree. Statement's tree should then be a block tree

    List<? extends StatementTree> statementsOfParent = parentBlock.getStatements();
    //for main 
    ArrayList<VariableTree> startingValuesToCheck = new ArrayList<VariableTree>();
    //for later revisit
    ArrayList<StatementTree> furtherInvestigationExpressions = new ArrayList<StatementTree>();
    ArrayList<IfTree> parentIfTrees = new ArrayList<IfTree>();
    ArrayList<SwitchTree> parentSwitchTrees = new ArrayList<SwitchTree>();

    StatementTree parentStatement = (StatementTree) state.getPath().getParentPath().iterator().next();
    int i = 0;
    boolean stopSearch = false;
    while(i<statementsOfParent.size() && !stopSearch)
    {
         StatementTree currStatement = statementsOfParent.get(i);
         if(currStatement.equals(parentStatement))
         {
             stopSearch = true;
         }
         else if(currStatement instanceof VariableTree)
         {
            //modifiers type name initializer ; which most of examples are...
            Name varName = ((VariableTree) currStatement).getName();
            IdentifierTree identifierFound = null;
            for(int j = 0; j < identifiersToFindIfSwitchFor.size(); j++)
            {
               if(varName.equals(identifiersToFindIfSwitchFor.get(j).getName()))
               {
                   identifierFound = identifiersToFindIfSwitchFor.get(j);
                   j = identifiersToFindIfSwitchFor.size();
               }
            }

           //is a variable initializer and has a valid value on the right
           //not of the useless form String sqlStatement;
           if(identifierFound != null && (((VariableTree) currStatement).getInitializer() != null))
           {
              identifiersToFindIfSwitchFor.remove(identifierFound);
              startingValuesToCheck.add((VariableTree) currStatement);
           }
           else if(((VariableTree) currStatement).getInitializer() != null)
           {
               furtherInvestigationExpressions.add(currStatement);
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
                furtherInvestigationExpressions.add(currStatement);
             }
             else if(exp instanceof CompoundAssignmentTree)
             {
                furtherInvestigationExpressions.add(currStatement);
             }
         }
 
        i++;
    }

    //startingvalues to check is an array of Variable trees where Name indicates the name of the variable they are
    //being assigned to. Main issue is the right side to look for identifiers.
    for(int k = 0; k < startingValuesToCheck.size(); k++)
    {
       VariableTree currTree = startingValuesToCheck.get(k);
       ExpressionTree rightSide = currTree.getInitializer();
       //going to only look at the simpler cases of var + var, and var
       if(rightSide instanceof BinaryTree)
       {
          ArrayList<IdentifierTree> identifiersToCheck = parseBinaryTreeForIdentifiers((BinaryTree) rightSide);
          for(int j = 0; j < identifiersToCheck.size(); j++)
          {
              identifiersToFindIfSwitchFor.add(identifiersToCheck.get(j));
          }
       }
       else if(rightSide instanceof IdentifierTree)
       {
          identifiersToFindIfSwitchFor.add((IdentifierTree) rightSide);
       }
    }

    boolean doneExpandingForIdentifiers = furtherInvestigationExpressions.isEmpty();
    int l = 0;
    while(!doneExpandingForIdentifiers)
    {
       if(furtherInvestigationExpressions.isEmpty() || identifiersToFindIfSwitchFor.isEmpty() || l >= 
          furtherInvestigationExpressions.size())
       {
           doneExpandingForIdentifiers = true;
       }
       else
       {
           StatementTree currStatement = furtherInvestigationExpressions.get(l);
           if(currStatement instanceof VariableTree)
           {
               Name assignedTo = ((VariableTree) currStatement).getName();
               IdentifierTree matchedIdentifierTree = null;
               for(int m = 0; m < identifiersToFindIfSwitchFor.size(); m++)
               {
                   if(identifiersToFindIfSwitchFor.get(m).getName().equals(assignedTo))
                   {
                       matchedIdentifierTree = identifiersToFindIfSwitchFor.get(m);
                       m = identifiersToFindIfSwitchFor.size();
                   }
               }
               //only if this statement eliminates a identifier do I remove the identifier and statment
               if(matchedIdentifierTree != null && ((VariableTree) currStatement).getInitializer() != null)
               {
                   ExpressionTree revisitExpression = ((VariableTree) currStatement).getInitializer();
                   if(revisitExpression instanceof IdentifierTree)
                   {
                       identifiersToFindIfSwitchFor.add((IdentifierTree) revisitExpression);
                   }
                   else if(revisitExpression instanceof BinaryTree)
                   {
                       ArrayList<IdentifierTree> idents = parseBinaryTreeForIdentifiers((BinaryTree) revisitExpression);
                       for(int n = 0; n < idents.size(); n++)
                       {
                           identifiersToFindIfSwitchFor.add(idents.get(i));
                       }
                   }

                   identifiersToFindIfSwitchFor.remove(matchedIdentifierTree);
                   furtherInvestigationExpressions.remove(currStatement);
               }
               else
               {
                  //move to next statement
                  l++;
               }
           }
           else
           {
               ExpressionTree exp = ((ExpressionStatementTree) currStatement).getExpression();
               if(exp instanceof AssignmentTree)
               {
                  ExpressionTree var = ((AssignmentTree) exp).getVariable();
                  ExpressionTree rightSide = ((AssignmentTree) exp).getExpression();
                  if(var instanceof IdentifierTree)
                  {
                       boolean identifierMatched = false;
                       for(int m = 0; m < identifiersToFindIfSwitchFor.size(); m++)
                       {
                           if(identifiersToFindIfSwitchFor.get(m).equals((IdentifierTree) var))
                           {
                              identifierMatched = true;
                              m = identifiersToFindIfSwitchFor.size();
                           }
                       }
                       if(identifierMatched)
                       {
                           if(rightSide instanceof IdentifierTree)
                           {
                              identifiersToFindIfSwitchFor.add((IdentifierTree) rightSide);
                           }
                           else if(rightSide instanceof BinaryTree)
                           {
                               ArrayList<IdentifierTree> idents = parseBinaryTreeForIdentifiers((BinaryTree) rightSide);
                               for(int n = 0; n < idents.size(); n++)
                               {
                                   identifiersToFindIfSwitchFor.add(idents.get(i));
                               }
                           }
                           
                           identifiersToFindIfSwitchFor.remove((IdentifierTree) var);
                           furtherInvestigationExpressions.remove(currStatement);
                       }
                       else
                       {
                          l++;
                       }
                  }
               }
               else if(exp instanceof CompoundAssignmentTree)
               {
                  //I know it's a code clone but sadly CompoundAssignmentTree and AssignmentTree do not have a parent
                  //that has Variable and Expression...
                  ExpressionTree var = ((CompoundAssignmentTree) exp).getVariable();
                  ExpressionTree rightSide = ((CompoundAssignmentTree) exp).getExpression();
                  if(var instanceof IdentifierTree)
                  {
                       boolean identifierMatched = false;
                       for(int m = 0; m < identifiersToFindIfSwitchFor.size(); m++)
                       {
                           if(identifiersToFindIfSwitchFor.get(m).equals((IdentifierTree) var))
                           {
                              identifierMatched = true;
                              m = identifiersToFindIfSwitchFor.size();
                           }
                       }
                       if(identifierMatched)
                       {
                           if(rightSide instanceof IdentifierTree)
                           {
                              identifiersToFindIfSwitchFor.add((IdentifierTree) rightSide);
                           }
                           else if(rightSide instanceof BinaryTree)
                           {
                               ArrayList<IdentifierTree> idents = parseBinaryTreeForIdentifiers((BinaryTree) rightSide);
                               for(int n = 0; n < idents.size(); n++)
                               {
                                   identifiersToFindIfSwitchFor.add(idents.get(i));
                               }
                           }
                           
                           identifiersToFindIfSwitchFor.remove((IdentifierTree) var);
                           furtherInvestigationExpressions.remove(currStatement);
                       }
                       else
                       {
                          l++;
                       }
                  }
               }
               else
               {
                  l++;
               }
           }
       }
    }
    
    //find if statements (remove from identifiers to find if switch for)
    if(identifiersToFindIfSwitchFor.isEmpty())
    {
       return NO_MATCH;
    }

    int y = 0;
    while(y < identifiersToFindIfSwitchFor.size())
    {
       IdentifierTree currIdent = identifiersToFindIfSwitchFor.get(y);
       boolean ifTreeFound = false;
       for(int w = 0; w < parentIfTrees.size(); w++)
       {
          IfTree currIf = parentIfTrees.get(w);
          ExpressionTree exp = currIf.getCondition();
          if(exp instanceof IdentifierTree)
          {
              if(((IdentifierTree) exp).equals(currIdent))
              {
                 ifTreeFound = true;
                 w= parentIfTrees.size();
              }
          }
          else if(exp instanceof BinaryTree)
          {
              ArrayList<IdentifierTree> idents = parseBinaryTreeForIdentifiers((BinaryTree) exp);
              for(int x = 0; x < idents.size(); x++)
              {
                  if(idents.get(x).equals(currIdent))
                  {
                     ifTreeFound = true;
                     w= parentIfTrees.size();
                     x = idents.size();
                  }
              }
          }
       }
       if(ifTreeFound)
       {
          identifiersToFindIfSwitchFor.remove(currIdent);
       }
       else
       {
          y++;
       }
    }

    //find switch setters (remove from identifiers to find if switch for)

    String identsThatStillNeedIfs = identifiersToFindIfSwitchFor.toString();
    if(!identifiersToFindIfSwitchFor.isEmpty())
    {
       return describeMatch(tree, SuggestedFix.builder().replace(0,0, identsThatStillNeedIfs).build());
    }

    if(!isViolating(tree,state))
    {
      return NO_MATCH;
    }

    return describeMatch(tree, SuggestedFix.builder().build());
  }

  private ArrayList<IdentifierTree> parseBinaryTreeForIdentifiers(BinaryTree tree)
  {
      ArrayList<IdentifierTree> listToReturn = new ArrayList<IdentifierTree>();
      ExpressionTree leftSide = tree.getLeftOperand();
      if(leftSide instanceof BinaryTree)
      {
          listToReturn = parseBinaryTreeForIdentifiers((BinaryTree)leftSide);
      }
      else if(leftSide instanceof IdentifierTree)
      {
          listToReturn.add((IdentifierTree) leftSide);
      }
      else if(leftSide instanceof MethodInvocationTree)
      {
          listToReturn = parseMethodInvocationTreeForIdentifier((MethodInvocationTree) leftSide);
          
      }

      ExpressionTree rightSide = tree.getRightOperand();
      ArrayList<IdentifierTree> rightIdentifiers = new ArrayList<IdentifierTree>();
      if(rightSide instanceof BinaryTree)
      {
          rightIdentifiers = parseBinaryTreeForIdentifiers((BinaryTree) rightSide);
      }
      else if(rightSide instanceof IdentifierTree)
      {
           rightIdentifiers.add((IdentifierTree) rightSide);
      }
      else if(rightSide instanceof MethodInvocationTree)
      {
          rightIdentifiers = parseMethodInvocationTreeForIdentifier((MethodInvocationTree) rightSide);
      }

      for(int i = 0; i < rightIdentifiers.size(); i++)
      {
           listToReturn.add(rightIdentifiers.get(i));
      }
      return listToReturn;
  }

  private ArrayList<IdentifierTree> parseMethodInvocationTreeForIdentifier(MethodInvocationTree tree)
  {
      ArrayList<IdentifierTree> listToReturn = new ArrayList<IdentifierTree>();
      ExpressionTree exp = tree.getMethodSelect();
      if(exp instanceof IdentifierTree)
      {
         listToReturn.add((IdentifierTree) exp);
      }
      else if(exp instanceof MethodInvocationTree)
      {
         listToReturn = parseMethodInvocationTreeForIdentifier((MethodInvocationTree) exp);
      }
      else if(exp instanceof MemberSelectTree)
      {
         ExpressionTree head = ((MemberSelectTree) exp).getExpression();
         if(head instanceof MethodInvocationTree)
         {
           listToReturn = parseMethodInvocationTreeForIdentifier((MethodInvocationTree) head);
         }
         else if(head instanceof IdentifierTree)
         {
            listToReturn.add((IdentifierTree) head);
         }
      } 
      return listToReturn;
  }

}
