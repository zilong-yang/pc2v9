/**
 * 
 */
package edu.csus.ecs.pc2.core.util;

import java.io.File;
import java.io.InputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * 
 * Transforms  xml and xsl input to generate the output.
 */
// $HeadURL$
public class XSLTransformer {

    /**
     * 
     */
    public XSLTransformer() {
        super();
    }

    public static void main(String[] args) {
        if (args.length == 3) {
            XSLTransformer me = new XSLTransformer();
            try {
                me.transform(args[0], args[1], new File(args[2]));
            } catch (Exception e) {
                System.err.println("Error doing tranform: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Usage: xslFile xmlFile outFile");
        }
    }

    /**
     * @param xslFile
     * @param xmlFile
     * @param outFile
     */
    public void transform(String xslFile, String xmlFile, File outFile) throws Exception {
        // Set up input documents
        Source inputXSL = new StreamSource(new File(xslFile));
        Source inputXML = new StreamSource(new File(xmlFile));
        
        transform(inputXSL, inputXML, outFile);
    }

    /**
     * @param xslFile
     * @param xmlStream
     * @param outFile
     */
    public void transform(String xslFile, InputStream xmlStream, File outFile) throws Exception {
        if (xmlStream == null) {
            throw new IllegalArgumentException("transform() Invalid xmlStream, cannot be null");
        }
        
        // Set up input documents
        Source inputXSL = new StreamSource(new File(xslFile));
        Source inputXML = new StreamSource(xmlStream);
        
        transform(inputXSL, inputXML, outFile);
    }

    /**
     * @param inputXSL
     * @param inputXML
     * @param outFile
     */
    public void transform(Source inputXSL, Source inputXML, File outFile) throws Exception {
        if (outFile == null) {
            throw new IllegalArgumentException("transform() Invalid outFile, cannot be null");
        }

        // Set up output sink
        Result output = new StreamResult(outFile);

        // Setup a factory for transforms
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        // Get a transofrmer for this XSL
        Transformer transformer = transformerFactory.newTransformer(inputXSL);

        // Perform the transformation
        transformer.transform(inputXML, output);
    }
}
