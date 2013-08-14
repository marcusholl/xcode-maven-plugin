/*
 * #%L
 * xcode-maven-plugin
 * %%
 * Copyright (C) 2012 SAP AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.sap.prd.mobile.ios.mios;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XCodeVersionInfoMojoTest
{

  @Test
  public void testTransformVersionsXmlRegular() throws IOException, ParserConfigurationException, SAXException,
        TransformerFactoryConfigurationError, TransformerException, XCodeException
  {
    File originalXml = new File("src/test/resources/validXML.xml");
    File transformedXml = File.createTempFile("testTransformVersionsXmlRegular", ".xml");
    
    new XCodeVersionInfoMojo().transformVersionsXml(originalXml, transformedXml);
    
    String original = toString(originalXml);
    String transformed = toString(transformedXml);
    System.out.println("ORIGINAL: "+original);
    System.out.println("TRANSFORMED: "+transformed);
    Assert.assertTrue("Does not contain 'PERFORCE_HOST': "+original, original.contains("PERFORCE_HOST"));
    Assert.assertFalse("Still contains 'PERFORCE_HOST': " + transformed, transformed.contains("PERFORCE_HOST"));
    Assert.assertTrue("Does not contain '<connection>PORT</connection>': " + transformed, transformed.contains("<connection>PORT</connection>"));
  }
  
  private String toString(File transformedXml) throws IOException
  {
    FileInputStream fileInputStream = new FileInputStream(transformedXml);
    try {
      return IOUtils.toString(fileInputStream);
    } finally {
      IOUtils.closeQuietly(fileInputStream);
    }
  }
}
