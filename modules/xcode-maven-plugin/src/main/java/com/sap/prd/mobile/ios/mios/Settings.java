package com.sap.prd.mobile.ios.mios;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Settings {
  private final static String CODE_SIGN_IDENTITY = "CODE_SIGN_IDENTITY";
  private final static String PROVISIONING_PROFILE = "PROVISIONING_PROFILE";
  private final static String DSTROOT = "DSTROOT";
  private final static String SYMROOT = "SYMROOT";
  private final static String SHARED_PRECOMPS_DIR = "SHARED_PRECOMPS_DIR";
  private final static String OBJROOT = "OBJROOT";
  private final static String XCODE_OUTPUT_DIRECTORY = "build";

  private final static List<String> MANAGED = Arrays.asList(CODE_SIGN_IDENTITY, PROVISIONING_PROFILE, DSTROOT, SYMROOT, SHARED_PRECOMPS_DIR, OBJROOT);
  final static Map<String, String> REQUIRED = new LinkedHashMap<String, String>(7);

  static {
      // Output directories should be specified (recommended by Apple - http://developer.apple.com/devcenter/download.action?path=/wwdc_2012/wwdc_2012_session_pdfs/session_404__building_from_the_command_line_with_xcode.pdf)
      REQUIRED.put(DSTROOT, XCODE_OUTPUT_DIRECTORY);
      REQUIRED.put(SYMROOT, XCODE_OUTPUT_DIRECTORY);
      REQUIRED.put(SHARED_PRECOMPS_DIR, XCODE_OUTPUT_DIRECTORY);
      REQUIRED.put(OBJROOT, XCODE_OUTPUT_DIRECTORY);
  }

  /**
   * @param userSettings to be validated.
   * @return the passed in userSettings if validation passed without exception
   * @throws IllegalArgumentException if the userSettings contain a key of an XCode setting that is managed by
   *            the plugin.
   */
  public static Map<String, String> validateUserSettings(Map<String, String> userSettings) {
      if (userSettings != null) {
          for (String key : userSettings.keySet()) {
              if (MANAGED.contains(key))
                  throw new IllegalArgumentException("XCode Setting " + key + " is managed by the plugin and cannot be modified by the user.");
          }
      }
      return userSettings;
  }

  static void appendSettings(XCodeContext xcodeContext, List<String> result) {
      if (xcodeContext.getCodeSignIdentity() != null && !xcodeContext.getCodeSignIdentity().isEmpty()) {
          appendSetting(result, Settings.CODE_SIGN_IDENTITY, xcodeContext.getCodeSignIdentity());
      }

      if (xcodeContext.getProvisioningProfile() != null) {
          appendSetting(result, Settings.PROVISIONING_PROFILE, xcodeContext.getProvisioningProfile());
      }

      Map<String, String> settings = xcodeContext.getSettings();

      for (Map.Entry<String, String> entry : settings.entrySet()) {
          appendSetting(result, entry.getKey(), entry.getValue());
      }
  }

  private static void appendSetting(List<String> result, String key, String value) {
      result.add(key + "=" + value);
  }
}
