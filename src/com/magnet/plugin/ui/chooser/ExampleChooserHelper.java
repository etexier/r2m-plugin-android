/*
 * Copyright (c) 2014 Magnet Systems, Inc.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.magnet.plugin.ui.chooser;

import com.intellij.openapi.ui.Messages;
import com.magnet.langpack.builder.rest.parser.ExampleParser;
import com.magnet.langpack.builder.rest.parser.RestExampleModel;
import com.magnet.plugin.helpers.IOUtils;
import com.magnet.plugin.helpers.Logger;
import com.magnet.plugin.helpers.UIHelper;
import com.magnet.plugin.helpers.URLHelper;
import com.magnet.plugin.messages.Rest2MobileMessages;
import com.magnet.plugin.models.ExampleResource;
import com.magnet.plugin.models.ExamplesManifest;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper for REST example chooser
 */
public class ExampleChooserHelper {

    private static final String MANIFEST_URL = ExampleResource.EXAMPLES_BASE_URL + "manifest.json";

    private static ExamplesManifest EXAMPLES_MANIFEST = null;

    private static volatile boolean EXAMPLES_DIALOG_UP = false;

    public static synchronized boolean isExamplesDialogUp() {
        return EXAMPLES_DIALOG_UP;
    }

    public static String showExamplesDialog() {

        EXAMPLES_DIALOG_UP = true;
        try {
            List<String> examples = getManifest().getExamplesList();
            String response = Messages.showEditableChooseDialog(
                    Rest2MobileMessages.getMessage(Rest2MobileMessages.CHOOSE_EXAMPLE_LABEL),
                    Rest2MobileMessages.getMessage(Rest2MobileMessages.CHOOSE_EXAMPLE_TITLE),
                    Messages.getQuestionIcon(),
                    examples.toArray(new String[examples.size()]),
                    Rest2MobileMessages.getMessage(Rest2MobileMessages.CHOOSE_EXAMPLE_DEFAULT_VALUE),
                    null);
            return response == null ? null : response.split(ExamplesManifest.DESCRIPTION_SEPARATOR_KEY)[0];
        } finally {
            EXAMPLES_DIALOG_UP = false;
        }
    }


    private static ExamplesManifest getManifest() {
        if (null != EXAMPLES_MANIFEST) {
            return EXAMPLES_MANIFEST;
        }
        InputStream infoStream = null;
        try {
            infoStream = URLHelper.loadUrl(MANIFEST_URL);
            String json = IOUtils.toString(infoStream);
            EXAMPLES_MANIFEST = new ExamplesManifest(json);
        } catch (Exception ex) {
            UIHelper.showErrorMessage("Couldn't parse manifest at URL: " + MANIFEST_URL);
            Logger.error(ExampleChooserHelper.class, ex.getMessage());
            EXAMPLES_MANIFEST = null;
        } finally {
            if (infoStream != null) {
                try {
                    infoStream.close();
                } catch (IOException e1) {
                    // ignore
                }
            }
        }
        return EXAMPLES_MANIFEST;
    }

    public static List<RestExampleModel> getControllersMethodsByName(String name) {
        String url = getExampleUrlByName(name);
        if (null == url) {
            return null;
        }
        return getControllersMethodsByUrl(url);
    }

    private static String getExampleUrlByName(String name) {
        ExamplesManifest manifest = getManifest();
        ExampleResource example = manifest.getExample(name);
        if (null == example) {
            return null;
        }
        return example.getUrl();

    }

    public static List<RestExampleModel> getControllersMethodsByUrl(String urlString) {
        ExampleParser parser = new ExampleParser();
        List<RestExampleModel> methodModels = new ArrayList<RestExampleModel>();
        try {
            URL url = new URL(urlString);
            methodModels.addAll(parser.parseExample(url));
        } catch (MalformedURLException e) {
            return null;
        }
        return methodModels;
    }

    public static List<RestExampleModel> getControllersMethodsByFile(File file) {
        List<RestExampleModel> methodModels = new ArrayList<RestExampleModel>();
        try {
            if (file.isFile()) {
                return getControllersMethodsByUrl(file.toURI().toURL().toString());
            }
            if (file.isDirectory()) {
                File[] files = file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isFile() && !file.getName().startsWith(".");
                    }
                });
                if (files != null && files.length > 0) {
                    for (File f: files) {
                        methodModels.addAll(getControllersMethodsByUrl(f.toURI().toURL().toString()));
                    }
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // should not happen
        }
        return methodModels;
    }

}