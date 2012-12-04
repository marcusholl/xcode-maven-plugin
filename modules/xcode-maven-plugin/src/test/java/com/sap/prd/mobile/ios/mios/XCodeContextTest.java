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
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

public class XCodeContextTest
{

  private static File projectDirectory;


  @BeforeClass
  public static void setup()
  {
    projectDirectory = new File(new File(".").getAbsoluteFile(), "src/test/projects/MyLibrary");
  }

  @Test
  public void testStraightForward()
  {

    final String projectName = "MyLibrary.xcodeproj";

    Map<String, String> managedOptions = new HashMap<String, String>();
    managedOptions.put(Options.ManagedOption.CONFIGURATION.toLowerCase(), "Release");
    managedOptions.put(Options.ManagedOption.SDK.toLowerCase(), "mysdk");
    managedOptions.put(Options.ManagedOption.PROJECT.toLowerCase(), projectName);
    Options options = new Options(null, managedOptions);

    HashMap<String, String> managedSettings = new HashMap<String, String>();
    managedSettings.put(Settings.ManagedSetting.CODE_SIGN_IDENTITY.name(), "MyCodeSignIdentity");
    managedSettings.put(Settings.ManagedSetting.PROVISIONING_PROFILE.name(), "MyProvisioningProfile");
    Settings settings = new Settings(null, managedSettings);

    final XCodeContext xCodeContext = new XCodeContext(Arrays.asList("clean",
          "build"), projectDirectory, System.out, settings, options);

    
    assertEquals(projectName, xCodeContext.getProjectName());
    assertArrayEquals(new String[] { "clean", "build" }, xCodeContext.getBuildActions().toArray());
    assertEquals("MyCodeSignIdentity", xCodeContext.getCodeSignIdentity());
    assertEquals("MyProvisioningProfile", xCodeContext.getProvisioningProfile());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyBuildActions()
  {
    new XCodeContext("MyLibrary", new ArrayList<String>(), projectDirectory, System.out);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildActionWithEmptyEntry()
  {
    new XCodeContext("MyLibrary", Arrays.asList("clean", "", "build"), projectDirectory, System.out);
  }

  @Test(expected = IllegalArgumentException.class)
  public void TestBuildActionEntryWithBlank()
  {
    new XCodeContext("MyLibrary", Arrays.asList("clean", "build foo"), projectDirectory, System.out);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildActionWithNullElement()
  {
    new XCodeContext("MyLibrary", Arrays.asList("clean", null, "build"), projectDirectory, System.out);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testXCodeContextWithEmptyProjectName()
  {
    new XCodeContext("", Arrays.asList("clean", "build"), projectDirectory, System.out);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testXCodeContextWithoutProjectName()
  {
    new XCodeContext(null, Arrays.asList("clean", "build"), projectDirectory, System.out);
   }


  @Test(expected = IllegalArgumentException.class)
  public void testXCodeContextWithoutPrintStream()
  {
    new XCodeContext("MyLibrary", Arrays.asList("clean", "build"), projectDirectory, null);
  }

  @Test
  public void testCodeSignIdentityIsNull() throws Exception
  {
    Map<String, String> managedOptions = new HashMap<String, String>();
    managedOptions.put(Options.ManagedOption.CONFIGURATION.toLowerCase(), "Release");
    managedOptions.put(Options.ManagedOption.SDK.toLowerCase(), "mysdk");
    managedOptions.put(Options.ManagedOption.PROJECT.toLowerCase(), "MyLibrary.xcodeproj");
    Options options = new Options(null, managedOptions);

    final XCodeContext xCodeContext = new XCodeContext(Arrays.asList("clean", "build"), projectDirectory,System.out, null, options);
    Assert.assertNull(xCodeContext.getCodeSignIdentity());
  }

  @Test
  public void testCodeSignIdentityIsEmpty() throws Exception
  {
    Map<String, String> managedOptions = new HashMap<String, String>();
    managedOptions.put(Options.ManagedOption.CONFIGURATION.toLowerCase(), "Release");
    managedOptions.put(Options.ManagedOption.SDK.toLowerCase(), "mysdk");
    managedOptions.put(Options.ManagedOption.PROJECT.toLowerCase(), "MyLibrary.xcodeproj");
    Options options = new Options(null, managedOptions);

    HashMap<String, String> managedSettings = new HashMap<String, String>();
    managedSettings.put(Settings.ManagedSetting.CODE_SIGN_IDENTITY.name(), "");
    Settings settings = new Settings(null, managedSettings);
    
    XCodeContext context = new XCodeContext(Arrays.asList("clean", "build"), projectDirectory, System.out, settings, options);
    
    assertEquals("", context.getCodeSignIdentity());
    
  }
  
  @Test
  public void testProvisioningProfileIsNull() throws Exception
  {
    Map<String, String> managedOptions = new HashMap<String, String>();
    managedOptions.put(Options.ManagedOption.CONFIGURATION.toLowerCase(), "Release");
    managedOptions.put(Options.ManagedOption.SDK.toLowerCase(), "mysdk");
    managedOptions.put(Options.ManagedOption.PROJECT.toLowerCase(), "MyLibrary.xcodeproj");
    Options options = new Options(null, managedOptions);

    final XCodeContext xCodeContext = new XCodeContext(Arrays.asList("clean", "build"), projectDirectory, System.out, null, options);
    Assert.assertNull(xCodeContext.getProvisioningProfile());
  }
}
