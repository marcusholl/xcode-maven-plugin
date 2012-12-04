package com.sap.prd.mobile.ios.mios;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Options {
  private final static String PROJECT_NAME = "project";
  private final static String CONFIGURATION = "configuration";
  private final static String SDK = "sdk";
  final static String TARGET = "target";
  private final static List<String> MANAGED = Arrays.asList(PROJECT_NAME, CONFIGURATION, SDK, TARGET);
  
  private Map<String, String> userOptions, managedOptions;

  Options(Map<String, String> userOptions, Map<String, String> managedOptions) {
    
    if(userOptions == null)
      this.userOptions = Collections.emptyMap();
    else
      this.userOptions = Collections.unmodifiableMap(new HashMap<String, String>(userOptions));
    
    if(managedOptions == null)
      this.managedOptions = Collections.emptyMap();
    else
      this.managedOptions = Collections.unmodifiableMap(new HashMap<String, String>(managedOptions));
    
    validateUserOptions(this.userOptions);
  }
  
  Map<String, String> getOptions() {
    final Map<String, String> result = new HashMap<String, String>();
    
    result.putAll(userOptions);
    result.putAll(managedOptions);
    
    return result;
  }
  /**
    * @param userOptions to be validated.
    * @return the passed in userOptions if validation passed without exception
    * @throws IllegalArgumentException if the userOptions contain a key of an XCode option that is managed by
    *            the plugin.
    */
   final static Map<String, String> validateUserOptions(Map<String, String> userOptions) {
       if (userOptions != null) {
           for (String key : userOptions.keySet()) {
               if (MANAGED.contains(key))
                   throw new IllegalArgumentException("XCode Option '" + key + "' is managed by the plugin and cannot be modified by the user.");
           }
       }
       return userOptions;
   }

  static void appendOptions(XCodeContext xcodeContext, List<String> result, String sdk, String configuration) {
      Map<String, String> options = xcodeContext.getOptions().getOptions();
      if (options != null) {
          for (Map.Entry<String, String> entry : options.entrySet()) {
              appendOption(result, entry.getKey(), entry.getValue());
          }
      }
      appendOption(result, PROJECT_NAME, xcodeContext.getProjectName() + XCodeConstants.XCODE_PROJECT_EXTENTION);
      appendOption(result, CONFIGURATION, configuration);
      if (sdk != null && !sdk.trim().isEmpty()) {
          appendOption(result, SDK, sdk);
      }
      if (xcodeContext.getTarget() != null && !xcodeContext.getTarget().isEmpty()) {
          appendOption(result, CommandLineBuilder.TARGET, xcodeContext.getTarget());
      }
  }

  private static void appendOption(List<String> result, String key, String value) {
     
     CommandLineBuilder.check(key, value);
     CommandLineBuilder.appendKey(result, key);
     CommandLineBuilder.appendValue(result, value);
 }

}

