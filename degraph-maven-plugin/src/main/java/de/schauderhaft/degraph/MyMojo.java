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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.schauderhaft.degraph.analysis.asm.Analyzer$;
import de.schauderhaft.degraph.configuration.Configuration;
import de.schauderhaft.degraph.configuration.Constraint;
import de.schauderhaft.degraph.configuration.CycleFree$;
import de.schauderhaft.degraph.configuration.NoPrinting;
import de.schauderhaft.degraph.configuration.Pattern;
import de.schauderhaft.degraph.graph.Graph;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Seq;

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

    public void execute() throws MojoExecutionException {
        File f = outputDirectory;
        if (!f.exists()) {
            f.mkdirs();
        }
        File touch = new File(f, "touch.txt");
        try(FileWriter w = new FileWriter(touch)) {
            w.write("touch.txt");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        }
    }

    public static void main(String[] args) {
        String classPath = "myclasspath";
        List<String> includes = Arrays.asList("/tmp");
        List<String> excludes = Arrays.asList("/tmp");
        Map<String, Seq<Pattern>> categories = Collections.emptyMap();
        Set<Constraint> constraint = Collections.singleton(CycleFree$.MODULE$);
        Configuration config = new Configuration(Option.apply(classPath),
                JavaConversions.asScalaBuffer(includes),
                JavaConversions.asScalaBuffer(excludes),
                ConversionHelper.toScalaMap(categories),
                new NoPrinting(),
                ConversionHelper.toScalaSet(constraint),
                Analyzer$.MODULE$);
        System.err.println("config: " + config);
        Graph graph = config.createGraph();
        System.err.println(graph);
    }
}
