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
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Context object for Xcode build to hold relevant data:
 * * projectName
 * * Set of configurations
 * * Set of SDKs
 * * List of buildActions
 * projectRootDirectory
 * codeSignIdentity
 * output stream
 * xcode options
 * xcode settings
 */
class XCodeContext
{
  enum SourceCodeLocation {ORIGINAL, WORKING_COPY};

  private final static String ls = System.getProperty("line.separator");

  private final String projectName;

  private final List<String> buildActions;

  private final File projectRootDirectory;

  private PrintStream out;

  private final String target;

  private final Options options;

  private final Settings settings;

  public XCodeContext(String projectName, List<String> buildActions,
        File projectRootDirectory, PrintStream out) {
    this(projectName, buildActions, projectRootDirectory, out, null, null, null);
}

  public XCodeContext(String projectName, List<String> buildActions,
        File projectRootDirectory, PrintStream out, String codeSignIdentity, String target)
  {
    this(projectName, buildActions, projectRootDirectory, out, target, null, null);
  }
  public XCodeContext(String projectName, List<String> buildActions,
        File projectRootDirectory, PrintStream out, String target, Settings settings, Options options)
  {
    super();

    raiseExceptionIfNullOrEmpty("projectName", projectName);
    raiseExceptionIfInvalid("buildActions", buildActions);

    if(projectRootDirectory == null || !projectRootDirectory.canRead())
      throw new IllegalArgumentException("ProjectRootDirectory '" + projectRootDirectory + "' is null or cannot be read.");

    this.projectName = projectName;
    this.buildActions = Collections.unmodifiableList(buildActions);
    this.projectRootDirectory = projectRootDirectory;
    setOut(out);
    this.target = target;
    
    if(settings == null) {
      Map<String, String> userSettings = new HashMap<String, String>(), managedSettings = new HashMap<String, String>();
      this.settings = new Settings(userSettings, managedSettings);
  } else {
      this.settings = settings;
    }
    
    if(options == null) {
      Map<String, String> userOptions = new HashMap<String, String>(), managedOptions = new HashMap<String, String>();
      this.options = new Options(userOptions, managedOptions);
    } else {
      this.options = options;
    }
  }

  public String getProjectName()
  {
    return projectName;
  }

  public List<String> getBuildActions()
  {
    return buildActions;
  }

  public String getCodeSignIdentity()
  {
    return settings.getSettings().get(Settings.CODE_SIGN_IDENTITY);
  }

  public File getProjectRootDirectory()
  {
    return projectRootDirectory;
  }

  public PrintStream getOut()
  {
    return out;
  }

  public final void setOut(PrintStream out)
  {
    if (out == null)
      throw new IllegalArgumentException("PrintStream for log handling is not available.");
    this.out = out;
  }

  public String getProvisioningProfile()
  {
    return getSettings().getSettings().get(Settings.PROVISIONING_PROFILE);
  }

  public String getTarget()
  {
    return target;
  }

    public Options getOptions() {
        return options;
    }

    public Settings getSettings() {
        return settings;
    }

   private static String toString(String prefix, Map<String, String> map, String separator) {
       if (map == null) return "";
       StringBuffer buffer = new StringBuffer();
       for (Map.Entry<String, String> entry : map.entrySet()){
           buffer.append(prefix).append(entry.getKey()).append(separator).append(entry.getValue());
       }
       return buffer.toString();
   }

    @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("ProjectRootDirectory: ").append(getProjectRootDirectory()).append(ls);
    sb.append("ProjectName         : ").append(getProjectName()).append(ls);
    sb.append("BuildActions        : ").append(buildActions).append(ls);
    sb.append("Target              : ").append(target).append(ls);
    sb.append("Options             : ").append(toString(" -", options.getOptions(), " "));
    sb.append("Settings            : ").append(toString(" ", settings.getSettings(), "="));
    return sb.toString();
  }


  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((buildActions == null) ? 0 : buildActions.hashCode());
    result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
    result = prime * result + ((projectRootDirectory == null) ? 0 : projectRootDirectory.hashCode());
    result = prime * result + ((target == null) ? 0 : target.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    XCodeContext other = (XCodeContext) obj;
    if (buildActions == null) {
      if (other.buildActions != null) return false;
    }
    else if (!buildActions.equals(other.buildActions)) return false;
    if (projectName == null) {
      if (other.projectName != null) return false;
    }
    else if (!projectName.equals(other.projectName)) return false;
    if (projectRootDirectory == null) {
      if (other.projectRootDirectory != null) return false;
    }
    else if (!projectRootDirectory.equals(other.projectRootDirectory)) return false;
    if (target == null) {
      if (other.target != null) return false;
    }
    else if (!target.equals(other.target)) return false;
    return true;
  }

  private static void raiseExceptionIfNullOrEmpty(final String key, final String value)
  {
    if (value == null || value.trim().length() == 0)
      throw new IllegalArgumentException(String.format(Locale.ENGLISH, "No %s provided. Was null or empty.", key));
  }

  private static void raiseExceptionIfInvalid(final String key, final Collection<String> collection)
  {

    if (collection == null || collection.size() == 0)
      throw new IllegalArgumentException("No build actions has been provided (Was either null or empty).");

    for (final String buildAction : collection) {

      if (buildAction == null || buildAction.length() == 0)
        throw new IllegalArgumentException("Build action array contained a null element or an empty element.");

      if (!buildAction.matches("[A-Za-z0-9_]+"))
        throw new IllegalArgumentException("Build action array contains an invalid element (" + buildAction + ").");
    }
  }
}
