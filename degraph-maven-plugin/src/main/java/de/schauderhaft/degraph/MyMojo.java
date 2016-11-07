package de.schauderhaft.degraph;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
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
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;

/**
 * Goal which touches a timestamp file.
 *
 * @deprecated Don't use!
 */
@Mojo(name = "touch", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class MyMojo extends AbstractMojo {
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;

    @Component
    private DependencyGraphBuilder dependencyGraphBuilder;

    @Component
    private ProjectBuilder projectBuilder;

    @Component
    private MavenProject mavenProject;

    @Component
    private MavenSession mavenSession;

    public void execute() throws MojoExecutionException {
        try {
            new ClasspathBuilder(dependencyGraphBuilder).collectProjectDependencies(mavenSession);
        } catch (DependencyGraphBuilderException ex) {
            MojoExecutionException newEx = new MojoExecutionException(ex.getLocalizedMessage());
            newEx.initCause(ex);
            throw newEx;
        }
    }

    public void execute0() throws MojoExecutionException {
        File f = outputDirectory;
        if (!f.exists()) {
            f.mkdirs();
        }
        File touch = new File(f, "touch.txt");
        try (FileWriter w = new FileWriter(touch)) {
            w.write("touch.txt");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        }
    }
}
