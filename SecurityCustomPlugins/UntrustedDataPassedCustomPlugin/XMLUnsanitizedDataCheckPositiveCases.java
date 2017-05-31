package com.google.errorprone.bugpatterns.testdata;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class XMLUnsanitizedDataCheckPositiveCases {

	// BUG: Diagnostic contains: Parameters passed to XML unsanitized
    public void violation1(BufferedOutputStream outStream, String quantity) 
    throws IOException
    {
        String xmlString;
        xmlString = "<item>\n<description>Widget</description>\n" +
        "<price>500.0</price>\n" +
        "<quantity>" + quantity + "</quantity></item>";
        outStream.write(xmlString.getBytes());
        outStream.flush();
    }

}
