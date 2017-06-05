package com.google.errorprone.bugpatterns;

import com.google.errorprone.*;

import static com.google.common.collect.Iterables.getLast;
import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.instanceMethod;
import static com.google.errorprone.matchers.Matchers.anyOf;

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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

@AutoService(BugChecker.class)

@BugPattern(
	name = "SQLUnsanitizedDataCheck",
	category = JDK,
	summary = "WARNING: Variable may not have been checked for constraints",
	severity = WARNING
)
public class SimpleUntrustedDataPassPlugin extends BugChecker implements MethodInvocationTreeMatcher{

  private Matcher<ExpressionTree> SQL_CONNENCTION_METHOD = instanceMethod().onDescendantOf(Statement.class.getName()).named("executeQuery");

  private Matcher<ExpressionTree> XML_METHOD = instanceMethod().onDescendantOf(BufferedOutputStream.class.getName()).named("write");

  private Matcher<ExpressionTree> RUNTIME_EXEC = instanceMethod().onDescendantOf(Runtime.class.getName()).named("exec");

  Matcher<ExpressionTree> methodsToLookOut = anyOf(SQL_CONNENCTION_METHOD,XML_METHOD,RUNTIME_EXEC);

  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if(!methodsToLookOut.matches(tree,state))
    {
      return NO_MATCH;
    }
    
    List<? extends ExpressionTree> args = tree.getArguments();
    for(int i = 0; i < args.size(); i++)
    {
       ExpressionTree currArg = args.get(i);
       if(currArg instanceof IdentifierTree)
       {
           return describeMatch(tree, SuggestedFix.builder().build());
       }
       else if(currArg instanceof BinaryTree)
       {
           ArrayList<IdentifierTree> trees = parseBinaryTreeForIdentifiers((BinaryTree) currArg);
           if(!trees.isEmpty())
           {
              return describeMatch(tree, SuggestedFix.builder().build());
           }
       }
       else if(currArg instanceof MethodInvocationTree)
       {
           ArrayList<IdentifierTree> ident = parseMethodInvocationTreeForIdentifier((MethodInvocationTree) currArg);
           if(!ident.isEmpty())
           {
              return describeMatch(tree, SuggestedFix.builder().build());
           }
       }
    }

    return NO_MATCH;
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
