package com.sap.prd.mobile.ios.mios;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class Settings {

  private final static String XCODE_OUTPUT_DIRECTORY = "build";
  
  enum ManagedSetting {CODE_SIGN_IDENTITY(false, null), PROVISIONING_PROFILE(false, null), DSTROOT(true, XCODE_OUTPUT_DIRECTORY), SYMROOT(true, XCODE_OUTPUT_DIRECTORY), SHARED_PRECOMPS_DIR(true, XCODE_OUTPUT_DIRECTORY), OBJROOT(true, XCODE_OUTPUT_DIRECTORY);

    private final boolean required;
    private String defaultValue;

    static ManagedSetting forName(String name) {
      
      for(ManagedSetting setting : values()) {
        if(setting.name().equals(name)) {
          return setting;
        }
      }

      return null;
    }
    ManagedSetting(boolean required, String defaultValue)
    {
      this.required = required;
      this.defaultValue = defaultValue;
    }

    boolean isRequired()
    {
      return required;
    }

    String getDefaultValue() {
      return defaultValue;
    }
  };

  final static Map<String, String> REQUIRED = new LinkedHashMap<String, String>(7);

  static {
    // Output directories should be specified (recommended by Apple - http://developer.apple.com/devcenter/download.action?path=/wwdc_2012/wwdc_2012_session_pdfs/session_404__building_from_the_command_line_with_xcode.pdf)
    for(ManagedSetting setting : ManagedSetting.values()) {
      if(setting.isRequired()) {
        REQUIRED.put(setting.name(), setting.getDefaultValue());
      }
    }
  }

  private Map<String, String> userSettings, managedSettings; 

  Settings(Map<String, String> userSettings, Map<String, String> managedSettings) {

    if(userSettings == null) {
      this.userSettings = Collections.emptyMap();
    } else {
      this.userSettings = Collections.unmodifiableMap(new HashMap<String, String>(userSettings));
    }

    validateUserSettings(this.userSettings);
    
    if(managedSettings == null) {
      this.managedSettings = Collections.unmodifiableMap(new HashMap<String, String>(REQUIRED));
    } else {

      Map<String, String> _managedSettings = new HashMap<String, String>();

      for(Map.Entry<String, String> e : managedSettings.entrySet()) {

        if(e.getKey() == null || e.getKey().trim().isEmpty())
          throw new IllegalArgumentException("Empty key found in settings. Value was: '" + e.getValue() + "'.");
        
        if(ManagedSetting.forName(e.getKey().trim()) == null)
          throw new IllegalArgumentException("Setting with key '" + e.getKey() + "' and value '" + e.getValue() + "' was provided. This setting is managed by the plugin" +
              "and must not be provided as managed setting.");

        if(e.getValue() == null) {
          
          if(e.getKey().equals(ManagedSetting.CODE_SIGN_IDENTITY.name())) {
            throw new IllegalArgumentException("CodesignIdentity was empty: '" + e.getValue()
                  + "'. If you want to use the code"
                  + " sign identity defined in the xCode project configuration just do"
                  + " not provide the 'codeSignIdentity' in your Maven settings.");
          }

          throw new IllegalArgumentException("No value provided for key '" + e.getKey() + "'.");
        }

        _managedSettings.put(e.getKey(), e.getValue());
      }
      _managedSettings.putAll(REQUIRED);
      this.managedSettings = Collections.unmodifiableMap(new HashMap<String, String>(_managedSettings));
    }
  }

  Map<String, String> getSettings() {
    Map<String, String> result = new HashMap<String, String>(this.userSettings.size() + this.managedSettings.size());
    result.putAll(this.userSettings);
    result.putAll(this.managedSettings);
    return Collections.unmodifiableMap(result);
  }

  /**
   * @param userSettings to be validated.
   * @return the passed in userSettings if validation passed without exception
   * @throws IllegalArgumentException if the userSettings contain a key of an XCode setting that is managed by
   *            the plugin.
   */
  private final static Map<String, String> validateUserSettings(Map<String, String> userSettings) {
    
    for(String key : userSettings.keySet()) {
      if(ManagedSetting.forName(key.trim()) != null) {
        throw new IllegalArgumentException("Setting '" + key + "' contained in user settings. This settings is managed by the plugin and must not be provided from outside.");
      }
    }
    return userSettings;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((managedSettings == null) ? 0 : managedSettings.hashCode());
    result = prime * result + ((userSettings == null) ? 0 : userSettings.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Settings other = (Settings) obj;
    if (managedSettings == null) {
      if (other.managedSettings != null) return false;
    }
    else if (!managedSettings.equals(other.managedSettings)) return false;
    if (userSettings == null) {
      if (other.userSettings != null) return false;
    }
    else if (!userSettings.equals(other.userSettings)) return false;
    return true;
  }
}
