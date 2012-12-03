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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class CommandLineBuilderTest
{
  private static File projectDirectory;


  @BeforeClass
  public static void setup()
  {
    projectDirectory = new File(new File(".").getAbsoluteFile(), "src/test/projects/MyLibrary");
  }

    private String appendStrings(String[] stringArray) {
       StringBuffer buffer = new StringBuffer();
       for (String string : stringArray) {
          buffer.append(string).append(" ");
       }
       return buffer.toString();
    }

    private void expect(XCodeContext context, String ... expected) {
        CommandLineBuilder commandLineBuilder = new CommandLineBuilder("Release", "mysdk", context);
        String[] actual = commandLineBuilder.createBuildCall();
        assertArrayEquals("\r\nExpected: "+appendStrings(expected)+"\r\nActual:   "+appendStrings(actual), expected, actual);
    }

  @Test
  public void testCommandlineBuilderStraightForward() throws Exception
  {
    XCodeContext context = new XCodeContext("MyLib", Arrays.asList("clean", "build"), projectDirectory, System.out);
    CommandLineBuilder commandLineBuilder = new CommandLineBuilder("Release", "mysdk", context);
    expect(context, "xcodebuild", "-project", "MyLib.xcodeproj", "-configuration", "Release", "-sdk",
            "mysdk", "OBJROOT=build", "SYMROOT=build", "DSTROOT=build", "SHARED_PRECOMPS_DIR=build", "clean", "build");
  }

    @Test
    public void testCommandlineBuilderStraightForwardSettings() throws Exception
    {
      Map<String, String> userSettings = new LinkedHashMap<String, String>();
      userSettings.put("VALID_ARCHS", "i386");
      userSettings.put("CONFIGURATION_BUILD_DIR", "/Users/me/projects/myapp/target/xcode/src/main/xcode/build");
      Settings settings = new Settings(userSettings, null);

      XCodeContext context = new XCodeContext("MyLib", Arrays.asList("clean", "build"), projectDirectory, System.out, null, null, null, settings, null);
      expect(context, "xcodebuild", "-project", "MyLib.xcodeproj", "-configuration", "Release", "-sdk",
              "mysdk", "OBJROOT=build", "SYMROOT=build", "DSTROOT=build", "CONFIGURATION_BUILD_DIR=/Users/me/projects/myapp/target/xcode/src/main/xcode/build", "SHARED_PRECOMPS_DIR=build", "VALID_ARCHS=i386", "clean", "build");
    }

    @Test
    public void testCommandlineBuilderStraightForwardOptions() throws Exception
    {
      
      Map<String, String> managedOptions = Collections.emptyMap();
      Map<String, String> userOptions = new LinkedHashMap<String, String>();
      userOptions.put("arch", "i386");
      
      Options options = new Options(managedOptions, userOptions);
      
      XCodeContext context = new XCodeContext("MyLib", Arrays.asList("clean", "build"), projectDirectory, System.out, null, null, null, null, options);
      expect(context, "xcodebuild", "-arch", "i386", "-project", "MyLib.xcodeproj", "-configuration", "Release", "-sdk",
              "mysdk", "OBJROOT=build", "SYMROOT=build", "DSTROOT=build", "SHARED_PRECOMPS_DIR=build", "clean", "build");
    }

  @Test
  public void testCodeSignIdentity() throws Exception
  {
    Map<String, String> userSettings = null, managedSettings = new HashMap<String, String>();
    managedSettings.put("CODE_SIGN_IDENTITY", "MyCodeSignIdentity");
    Settings settings = new Settings(userSettings, managedSettings);
    
    XCodeContext context = new XCodeContext("MyLib", Arrays.asList("clean", "build"), projectDirectory, System.out, "MyCodeSignIdentity", null, null, settings, null);
    CommandLineBuilder commandLineBuilder = new CommandLineBuilder("Release", "mysdk", context);
    assertTrue(Arrays.asList(commandLineBuilder.createBuildCall()).contains("CODE_SIGN_IDENTITY=MyCodeSignIdentity"));
  }

  @Test
  public void testCodeSignIdentityIsNull() throws Exception
  {

    Map<String, String> userSettings = null, managedSettings = new HashMap<String, String>();
    managedSettings.put("CODE_SIGN_IDENTITY", null);
    Settings settings = new Settings(userSettings, managedSettings);

    XCodeContext context = new XCodeContext("MyLib", Arrays.asList("clean", "build"), projectDirectory, System.out, null, null, null, settings, null);
    CommandLineBuilder commandLineBuilder = new CommandLineBuilder("Release", "mysdk", context);

    for (String param : commandLineBuilder.createBuildCall()) {
      assertFalse("The command line must not contain a parameter 'CODE_SIGN_IDENTITY='",
            param.contains("CODE_SIGN_IDENTITY="));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCodeSignIdentityIsEmpty() throws Exception
  {
    XCodeContext context = new XCodeContext("MyLib", Arrays.asList("clean", "build"), null, System.out, "", null, null);
    new CommandLineBuilder("Release", "mysdk", context);
  }

  
  @Test
  public void testProvisioningProfile() throws Exception
  {
    Map<String, String> userSettings = null, managedSettings = new HashMap<String, String>();
    managedSettings.put("PROVISIONING_PROFILE", "MyProvisioningProfile");
    Settings settings = new Settings(userSettings, managedSettings);

    XCodeContext context = new XCodeContext("MyLib", Arrays.asList("clean", "build"), projectDirectory, System.out, null, "MyProvisioningProfile", null, settings, null);
    CommandLineBuilder commandLineBuilder = new CommandLineBuilder("Release", "mysdk", context);
    assertTrue(Arrays.asList(commandLineBuilder.createBuildCall()).contains("PROVISIONING_PROFILE=MyProvisioningProfile"));
  }

  @Test
  public void testProvisioningProfileIsNull() throws Exception
  {
    XCodeContext context = new XCodeContext("MyLib", Arrays.asList("clean", "build"), projectDirectory, System.out, null, null, null);
    CommandLineBuilder commandLineBuilder = new CommandLineBuilder("Release", "mysdk", context);
    for (String param : commandLineBuilder.createBuildCall()) {
      assertFalse("The command line must not contain a parameter 'PROVISIONING_PROFILE='",
            param.contains("PROVISIONING_PROFILE="));
    }
  }
}
