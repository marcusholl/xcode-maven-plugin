package com.sap.prd.mobile.ios.mios;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Settings {
  final static String CODE_SIGN_IDENTITY = "CODE_SIGN_IDENTITY";
  final static String PROVISIONING_PROFILE = "PROVISIONING_PROFILE";
  private final static String DSTROOT = "DSTROOT";
  private final static String SYMROOT = "SYMROOT";
  private final static String SHARED_PRECOMPS_DIR = "SHARED_PRECOMPS_DIR";
  private final static String OBJROOT = "OBJROOT";
  private final static String XCODE_OUTPUT_DIRECTORY = "build";

  private final static List<String> MANAGED = Arrays.asList(CODE_SIGN_IDENTITY, PROVISIONING_PROFILE, DSTROOT, SYMROOT, SHARED_PRECOMPS_DIR, OBJROOT);
  final static Map<String, String> REQUIRED = new LinkedHashMap<String, String>(7);

  private Map<String, String> userSettings, managedSettings; 
  
  static {
      // Output directories should be specified (recommended by Apple - http://developer.apple.com/devcenter/download.action?path=/wwdc_2012/wwdc_2012_session_pdfs/session_404__building_from_the_command_line_with_xcode.pdf)
      REQUIRED.put(DSTROOT, XCODE_OUTPUT_DIRECTORY);
      REQUIRED.put(SYMROOT, XCODE_OUTPUT_DIRECTORY);
      REQUIRED.put(SHARED_PRECOMPS_DIR, XCODE_OUTPUT_DIRECTORY);
      REQUIRED.put(OBJROOT, XCODE_OUTPUT_DIRECTORY);
  }

  Settings(Map<String, String> userSettings, Map<String, String> managedSettings) {
    
    if(userSettings == null) {
      this.userSettings = Collections.emptyMap();
    } else {

      for(String key :userSettings.keySet())
        if(MANAGED.contains(key))
          throw new IllegalArgumentException("Setting '" + key + "' contained in user settings. This settings is managed by the plugin and must not be provided from outside.");

      this.userSettings = Collections.unmodifiableMap(new HashMap<String, String>(userSettings));
    }
    
    if(managedSettings == null) {
      this.managedSettings = Collections.unmodifiableMap(new HashMap<String, String>(REQUIRED));
    } else {
      
      Map<String, String> _managedSettings = new HashMap<String, String>();
      
      for(Map.Entry<String, String> e : managedSettings.entrySet()) {

        if(e.getKey() == null || e.getKey().trim().isEmpty())
          throw new IllegalArgumentException("Empty key found in settings. Value was: '" + e.getValue() + "'.");
        
        if(e.getValue() == null) {
          
          if(e.getKey().equals(CODE_SIGN_IDENTITY)) {
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
  static Map<String, String> validateUserSettings(Map<String, String> userSettings) {
      if (userSettings != null) {
          for (String key : userSettings.keySet()) {
              if (MANAGED.contains(key))
                  throw new IllegalArgumentException("XCode Setting " + key + " is managed by the plugin and cannot be modified by the user.");
          }
      }
      return userSettings;
  }

  static void appendSettings(Settings settings, List<String> result) {

       for (Map.Entry<String, String> entry : settings.getSettings().entrySet()) {
          appendSetting(result, entry.getKey(), entry.getValue());
      }
  }

  private static void appendSetting(List<String> result, String key, String value) {
      result.add(key + "=" + value);
  }
}
