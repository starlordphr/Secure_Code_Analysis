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

@AutoService(BugChecker.class)

@BugPattern(
	name = "XMLUnsanitizedDataCheck",
	category = JDK,
	summary = "Untrusted Data not checked for constraints before being passed into XML Command",
	severity = ERROR
)


public class XMLUnsanitizedDataCheck extends UnsanitizedUntrustedDataPassedCheck
{
    LANGUAGE_METHOD = instanceMethod().onDescendantOf(BufferedOutputStream.class.getName()).named("write");

    private SuggestedFix getCorrection(List<? extends ExpressionTree> fixArgs)
    {
       //Suggested fixes are if statement, case statement, schema check (static schema! useless if from a variable...)

       return SuggestedFix.builder()
            .replace(
                ((JCTree) tree).getStartPosition(),
                ((JCTree) formatArgs.get(0)).getStartPosition(),
                "System.err.printf(")
            .replace(
                state.getEndPosition((JCTree) getLast(formatArgs)),
                state.getEndPosition((JCTree) tree),
                ")")
            .build();
    }

    private boolean isViolating(Symbol callerClassSymbol, MethodInvocationTree  tree, VisitorState state)
    {
        //check if valid schema check exists & is being used to validate the variable
	return false;
    }

}
