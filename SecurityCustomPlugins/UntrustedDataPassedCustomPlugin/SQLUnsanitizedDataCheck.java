package com.google.errorprone.bugpatterns;

import static com.google.common.collect.Iterables.getLast;

import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.instanceMethod;
import com.google.errorprone.matchers.Matcher;
import com.sun.source.tree.*;
import com.google.errorprone.VisitorState;
import com.google.errorprone.BugPattern;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.auto.service.AutoService;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

@AutoService(BugChecker.class)

@BugPattern(
	name = "SQLUnsanitizedDataCheck",
	category = JDK,
	summary = "WARNING: Untrusted data may not have been checked for constraints before being passed into SQL Command",
	severity = WARNING
)
public class SQLUnsanitizedDataCheck extends AbstractUnsanitizedUntrustedDataPassedCheck
{
    Matcher<ExpressionTree> LANGUAGE_METHOD = instanceMethod().onDescendantOf(Statement.class.getName()).named("executeQuery");

    //list of dangerous SQL operations that can expose a security risk
    private final static List<String> dangerSQLStrings = new ArrayList<>(Arrays.asList(
            "SELECT",
            "INSERT", 
            "DELETE",
            "ALTER", 
            "DROP", 
            "CREATE",
            "USE", 
            "SHOW", 
            "ALTER"));

    Matcher<ExpressionTree> getLanguageMethod()
    {
       return LANGUAGE_METHOD;
    }

    boolean isViolating(MethodInvocationTree  tree, VisitorState state)
    {
        //check if danger SQL strings are in the parameters with a variable in the string, if yes, trouble! (ignore case)
        List<? extends ExpressionTree> args = tree.getArguments();
        ExpressionTree dangerWordFinder = (ExpressionTree) args.get(0);
        boolean keepParsing = true;
        
        while(keepParsing)
        {
           if(dangerWordFinder instanceof BinaryTree)
           {
               dangerWordFinder = ((BinaryTree) dangerWordFinder).getLeftOperand();
           }
           else if(dangerWordFinder instanceof LiteralTree)
           {
               String sqlCommand = ((String) ((LiteralTree) dangerWordFinder).getValue()).toUpperCase();
               for(int i = 0; i < dangerSQLStrings.size(); i++)
               {
                  if(sqlCommand.contains(dangerSQLStrings.get(i)))
                  {
                     return true;
                  }
               }
               
               keepParsing = false;
           }
           else
           {
               keepParsing = false;
           }
        }

        if(dangerWordFinder instanceof ConditionalExpressionTree)
        {
            ExpressionTree ifTrue = ((ConditionalExpressionTree) dangerWordFinder).getTrueExpression();
            String trueSQLCommand = ((String) ((LiteralTree) ifTrue).getValue()).toUpperCase();
            ExpressionTree ifFalse = ((ConditionalExpressionTree) dangerWordFinder).getFalseExpression();
            String falseSQLCommand = ((String) ((LiteralTree) ifFalse).getValue()).toUpperCase();

            //check both sides of the coin at the same time.
            for(int i = 0; i < dangerSQLStrings.size(); i++)
            {
                  if(trueSQLCommand.contains(dangerSQLStrings.get(i)) || falseSQLCommand.contains(dangerSQLStrings.get(i)))
                  {
                     return true;
                  }
             }
        }
        
	return false;
    }

}
