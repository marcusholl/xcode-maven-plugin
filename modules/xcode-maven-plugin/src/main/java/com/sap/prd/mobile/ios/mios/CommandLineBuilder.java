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

import java.util.*;

class CommandLineBuilder {

    private final static String XCODEBUILD = "xcodebuild";
    final static String TARGET = "target";

    private XCodeContext xcodeContext;

    public CommandLineBuilder(XCodeContext ctx) {
        this.xcodeContext = ctx;
    }

    String[] createBuildCall() {
        List<String> result = createBaseCall();
        for (String buildAction : xcodeContext.getBuildActions()) {
            appendValue(result, buildAction);
        }
        return result.toArray(new String[result.size()]);
    }

    String[] createShowBuildSettingsCall() {
        List<String> result = createBaseCall();
        appendKey(result, "showBuildSettings");
        return result.toArray(new String[result.size()]);
    }

    private List<String> createBaseCall() {
        List<String> result = new ArrayList<String>();
        result.add(XCODEBUILD);
        Options.appendOptions(xcodeContext, result);
        Settings.appendSettings(xcodeContext.getSettings(), result);
        return result;
    }

    static void appendKey(List<String> result, String key) {
        check("key", key);
        result.add("-" + key);
    }

    static void appendValue(List<String> result, String value) {
        check("value", value);
        result.add(value);
    }

    static void check(final String name, final String forCheck) {
        if (forCheck == null || forCheck.isEmpty())
            throw new IllegalStateException("Invalid " + name + ": '" + forCheck + "'. Was null or empty.");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(256);
        boolean first = true;
        for (String part : createBuildCall()) {
            if (!first)
                sb.append(" ");
            else
                first = false;
            sb.append(part);
        }
        return sb.toString();
    }
}
