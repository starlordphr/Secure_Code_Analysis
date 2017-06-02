package com.google.errorprone.bugpatterns;

import static com.google.common.collect.Iterables.getLast;

import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
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
	summary = "Untrusted Data not checked for constraints before being passed into SQL Command",
	severity = ERROR
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

    boolean isViolating(MethodTree  tree, VisitorState state)
    {
        //check if danger SQL strings are in the parameters with a variable in the string, if yes, trouble! (ignore case)
        //look at current string being fed into language method and find
        //need to visit parent tree and find if the parent has if & else statements for variables...
        //may want to use tree scanner
	return false;
    }

}
