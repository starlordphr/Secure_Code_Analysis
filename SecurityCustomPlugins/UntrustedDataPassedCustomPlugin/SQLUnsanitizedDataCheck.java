import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@AutoService(BugChecker.class)

@BugPattern(
	name = "SQLUnsanitizedDataCheck",
	category = JDK,
	summary = "Untrusted Data not checked for constraints before being passed into SQL Command",
	severity = ERROR
)


public class SQLUnsanitizedDataCheck extends AbstractUnsanitizedUntrustedDataPassedCheck
{
    LANGUAGE_METHOD = instanceMethod().onDescendantOf(Connection.class.getName()).named("executeQuery");

    //list of dangerous SQL operations that can expose a security risk
    private List<String> dangerSQLStrings = new ArrayList<>(Arrays.asList("SELECT", "INSERT", "DELETE", "ALTER", "DROP", 
         "CREATE", "USE", "SHOW", "ALTER"));

    SuggestedFix getCorrection(List<? extends ExpressionTree> fixArgs)
    {
       //Suggested fixes are if statement or case statement. And use PreparedStatement's .setString() method
       return SuggestedFix.builder()
            .replace(
                ((JCTree) tree).getStartPosition(),
                ((JCTree) formatArgs.get(0)).getStartPosition(),
                "PreparedStatement ")
            .replace(
                state.getEndPosition((JCTree) getLast(formatArgs)),
                state.getEndPosition((JCTree) tree),
                ")")
            .build();
    }

    boolean isViolating(MethodInvocationTree  tree, VisitorState state)
    {
        //check if danger SQL strings are in the parameters with a variable in the string, if yes, trouble! (ignore case)
        //look at current string being fed into language method and find
        //need to visit parent tree and find if the parent has if & else statements for variables...
        //may want to use tree scanner
	return false;
    }

}
