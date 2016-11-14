package de.schauderhaft.degraph;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.schauderhaft.degraph.configuration.ConstraintViolation;
import de.schauderhaft.degraph.configuration.CycleFree$;
import de.schauderhaft.degraph.configuration.NoPrinting;
import de.schauderhaft.degraph.configuration.Pattern;
import de.schauderhaft.degraph.configuration.Print;
import de.schauderhaft.degraph.configuration.PrintConfiguration;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "degraph", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresDependencyResolution = ResolutionScope.TEST)
public class DegraphMojo extends AbstractMojo {
    @Component
    private MavenProject mavenProject;

    @Parameter(property = "outputFilename" )
    private String outputFilename;

    @Parameter(property = "includes" )
    private String includes;

    @Parameter(property = "excludes" )
    private String excludes;

    @Parameter(property = "analyzeArtifacts" )
    private Boolean analyzeArtifacts;

    @Parameter(property = "violationFree" )
    private Boolean requireViolationFree;

    public void execute() throws MojoExecutionException {
        try {
            String classpath = ClasspathBuilder.buildClasspathString(mavenProject, analyzeArtifacts != null && analyzeArtifacts);
            //TODO: introduce type parameter (here walk dragons)
            Set constraint = Collections.singleton(CycleFree$.MODULE$);
            DegraphJavaConfig config = new DegraphJavaConfig(classpath, includes(),
                    excludes(), Collections.<String, List<Pattern>>emptyMap(),
                    createPrintConfiguration(), constraint);
            DegraphJavaAdapter javaAdapter = new DegraphJavaAdapter(config);
            javaAdapter.analyze();
            if (outputFilename != null) {
                javaAdapter.storeGraph();
            }
            System.err.println("requireViolationFree: " + requireViolationFree);
            if (requireViolationFree != null && requireViolationFree) {
                List<ConstraintViolation> violations = javaAdapter.getViolations();
                System.err.println("violations: " + violations);
                if (!violations.isEmpty()) {
                    throw new MojoExecutionException("there are dependency violations: " +  violations);
                }
            }
        } catch (DependencyResolutionRequiredException ex) {
            MojoExecutionException newEx = new MojoExecutionException(ex.getLocalizedMessage());
            newEx.initCause(ex);
            throw newEx;
        }
    }

    private List<String> includes() {
        return stringToSingletonList(includes);
    }

    private List<String> excludes() {
        return stringToSingletonList(excludes);
    }

    private static List<String> stringToSingletonList(String src) {
        return src == null || src.isEmpty() ? Collections.<String>emptyList() : Arrays.asList(src);
    }

    private PrintConfiguration createPrintConfiguration() {
        return outputFilename == null ? NoPrinting.apply() : new Print(outputFilename, true);
    }
}
