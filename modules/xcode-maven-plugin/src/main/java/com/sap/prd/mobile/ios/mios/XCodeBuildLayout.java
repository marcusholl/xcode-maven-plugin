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

import org.apache.maven.plugin.logging.Log;


/**
 * Helper methods for Xcode build to retrieve certain directories
 *
 */
class XCodeBuildLayout
{

  //
  // TODO cleanup, reuse the methods
  //
  // TODO: we should introduce a class that encapsultes all parameters that
  // are relevant for distiguishing build artifacts.
  //

  static File getBinary(final File buildDir, final String configuration, final String sdk, final String projectName)
  {

    return new File(buildDir, configuration + "-" + sdk + "/lib" + projectName + ".a");
  }

  static File getBundleDirectory(final File srcDir, final String bundleName)
  {
    return new File(srcDir, bundleName + ".bundle");
  }

  static File getConfigurationBuildDir(final XCodeContext context, final Log log) throws XCodeException
  {
    return new File(getEffectiveBuildSetting(context, log, EffectiveBuildSettings.CONFIGURATION_BUILD_DIR));
  }

  static File getBuildDir(final XCodeContext context, final Log log) throws XCodeException
  {
    return new File(getEffectiveBuildSetting(context, log, EffectiveBuildSettings.SYMROOT));
  }
  
  private static String getEffectiveBuildSetting(final XCodeContext context, final Log log, final String name) throws XCodeException{

    String value = EffectiveBuildSettings.getBuildSetting(context, log, name);

    if(value == null || value.trim().isEmpty()) {
      throw new XCodeException("Parameter '" + name + "' was null or empty (" + value + ") for " + context + ".");
    }
    return value;
  }
}
