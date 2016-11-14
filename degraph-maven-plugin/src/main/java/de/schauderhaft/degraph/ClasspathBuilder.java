package de.schauderhaft.degraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

public class ClasspathBuilder {
    public static String buildClasspathString(MavenProject mavenProject, boolean addArtifacts) throws DependencyResolutionRequiredException {
        List<String> artifactFiles = new ArrayList<>();
        if (addArtifacts) {
            List<Artifact> artifacts = mavenProject.getRuntimeArtifacts();
            for (Artifact artifact : artifacts) {
                artifactFiles.add(artifact.getFile().getAbsolutePath());
            }
        }
        artifactFiles.add(mavenProject.getBuild().getOutputDirectory());
        return join(artifactFiles, ":");
    }

    public static String join(Iterable<String> iterable, String delimiter) {
        Iterator<String> iter = iterable.iterator();
        StringBuilder result = new StringBuilder();
        if (iter.hasNext()) {
            result.append(iter.next());
        }
        while (iter.hasNext()) {
            result.append(delimiter).append(iter.next());
        }
        return result.toString();
    }
}
