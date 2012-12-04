package com.sap.prd.mobile.ios.mios;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class Options {

  enum ManagedOption {
    PROJECT(true), CONFIGURATION(true), SDK(false), TARGET(false);

    static ManagedOption forName(String name) {
      for (ManagedOption value : values()) {
        if(value.name().equals(name)) {
          return value;
        }
      }
      return null;
    }
    
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
   private final static Map<String, String> validateUserOptions(Map<String, String> userOptions) {

     for(ManagedOption option : ManagedOption.values()) {
       if(userOptions.keySet().contains(option.toLowerCase()))
         throw new IllegalArgumentException("XCode Option '" + option.toLowerCase() + "' is managed by the plugin and cannot be modified by the user.");
     }

     return userOptions;
   }

   private final static Map<String, String> validateManagedOptions(Map<String, String> managedOptions) {

     for(ManagedOption option : ManagedOption.values()) {
       if(option.isRequired() && !managedOptions.containsKey(option.toLowerCase()))
         throw new IllegalArgumentException("Required option '" + option.toLowerCase() + "' was not available inside the managed options.");
     }

     for(String key : managedOptions.keySet()) {
       if(ManagedOption.forName(key.toUpperCase()) == null)
         throw new IllegalArgumentException("Option '" + key + "' is not managed by the plugin. This option must not be provided as managed option.");
     }

     return managedOptions;
   }

  @Override
  public String toString()
  {
    return "Options [userOptions=" + userOptions + ", managedOptions=" + managedOptions + "]";
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((managedOptions == null) ? 0 : managedOptions.hashCode());
    result = prime * result + ((userOptions == null) ? 0 : userOptions.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Options other = (Options) obj;
    if (managedOptions == null) {
      if (other.managedOptions != null) return false;
    }
    else if (!managedOptions.equals(other.managedOptions)) return false;
    if (userOptions == null) {
      if (other.userOptions != null) return false;
    }
    else if (!userOptions.equals(other.userOptions)) return false;
    return true;
  }
  
  

}

