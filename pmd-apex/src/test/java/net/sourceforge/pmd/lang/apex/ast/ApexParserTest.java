package net.sourceforge.pmd.lang.apex.ast;

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.apex.ApexParserOptions;
import net.sourceforge.pmd.lang.ast.Node;

public class ApexParserTest {

    @Test
    public void understandsSimpleFile() {
    	
    	// Setup
    	String code = "public class SimpleClass {\n" +
		                "    public void methodWithManyParams(String a, String b, String c, String d, String e, String f, String g) {\n" +
		                "        \n" +
		                "    }\n" +
		                "}";
    	
    	// Exercise
        ASTUserClass rootNode = parse(code);
        //dumpNode(rootNode);

        // Verify
        List<ASTMethod> methods = rootNode.findDescendantsOfType(ASTMethod.class);
        assertEquals(4, methods.size());
    }
    
    
    @Test
    public void parsesRealWorldClasses() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("fflib_SObjectDomain.cls").getFile());
			String sourceCode = FileUtils.readFileToString(file);
			ASTUserClass rootNode = parse(sourceCode);
	        //dumpNode(rootNode);
		}
		catch (IOException e) {
			Assert.fail();
		}
        
    }
    
    // TEST HELPER
    
    private ASTUserClass parse(String code) {
        ApexParser parser = new ApexParser(new ApexParserOptions());
        Reader reader = new StringReader(code);
        return (ASTUserClass) parser.parse(reader);
    }

    private void dumpNode(Node node) {
        DumpFacade facade = new DumpFacade();
        StringWriter writer = new StringWriter();
        facade.initializeWith(writer, "", true, (ApexNode<?>)node);
        facade.visit((ApexNode<?>)node, "");
        System.out.println(writer.toString());
    }
}
