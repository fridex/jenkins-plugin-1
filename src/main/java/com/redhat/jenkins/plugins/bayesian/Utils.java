package com.redhat.jenkins.plugins.bayesian;

/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *limitations under the License.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hudson.FilePath;

/* package */ class Utils {

    @SuppressWarnings("serial")
    private static List<String> knownNpmManifests = new ArrayList<String>() {
        {
            add("package.json");
            add("npm-shrinkwrap.json");
        }
    };

    @SuppressWarnings("serial")
    private static List<String> knownMavenManifests = new ArrayList<String>() {
        {
            add("pom.xml");
        }
    };

    public static List<FilePath> findManifests(FilePath workspace) {
        List<FilePath> manifests = new ArrayList<FilePath>();

        // Maven
        List<FilePath> mavenManifests = findManifestsFromList(workspace, knownMavenManifests);
        if (!mavenManifests.isEmpty()) {
            // TODO: get all poms
            FilePath pom = workspace.child("target/stackinfo/poms/pom.xml");
            if (manifestExists(pom)) {
                manifests.add(pom);
            }
        }

        // NPM
        List<FilePath> npmManifests = findManifestsFromList(workspace, knownNpmManifests);
        manifests.addAll(npmManifests);

        return manifests;
    }

    private static List<FilePath> findManifestsFromList(FilePath workspace, List<String> manifests) {
        List<FilePath> result = new ArrayList<FilePath>();

        for (String manifest: manifests) {
            FilePath manifestFile = workspace.child(manifest);
            if (manifestExists(manifestFile)) {
                result.add(manifestFile);
            }
        }
        return result;
    }

    private static boolean manifestExists(FilePath manifestFile) {
        try {
            if (manifestFile.exists()) {
                return true;
            }
        } catch (IOException | InterruptedException e) {
            // TODO log
        }
        return false;
    }
}
