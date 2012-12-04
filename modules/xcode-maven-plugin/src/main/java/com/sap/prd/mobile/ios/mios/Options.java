package com.sap.prd.mobile.ios.mios;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Options {


  enum ManagedOption {
    PROJECT(true), CONFIGURATION(true), SDK(false), TARGET(false);

    private final boolean required;
    ManagedOption(boolean required) {
      this.required = required;
    }

    String toLowerCase() {
      return name().toLowerCase();
    }

    boolean isRequired() {
      return required;
    }
  }

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

    validateManagedOptions(this.managedOptions);
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

     for(ManagedOption option : ManagedOption.values()) {
       if(userOptions.keySet().contains(option.toLowerCase()))
         throw new IllegalArgumentException("XCode Option '" + option.toLowerCase() + "' is managed by the plugin and cannot be modified by the user.");
     }

     return userOptions;
   }

   final static Map<String, String> validateManagedOptions(Map<String, String> managedOptions) {

     for(ManagedOption option : ManagedOption.values()) {
       if(option.isRequired() && !managedOptions.containsKey(option.toLowerCase()))
         throw new IllegalArgumentException("Required option '" + option.toLowerCase() + "' was not available inside the managed options.");
     }

     return managedOptions;
   }

  static void appendOptions(XCodeContext xcodeContext, List<String> result) {
      Map<String, String> options = xcodeContext.getOptions().getOptions();
      if (options != null) {
          for (Map.Entry<String, String> entry : options.entrySet()) {
              appendOption(result, entry.getKey(), entry.getValue());
          }
      }
  }

  private static void appendOption(List<String> result, String key, String value) {
     
     CommandLineBuilder.check(key, value);
     CommandLineBuilder.appendKey(result, key);
     CommandLineBuilder.appendValue(result, value);
 }

}

