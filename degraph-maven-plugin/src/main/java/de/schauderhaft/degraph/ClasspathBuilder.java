package de.schauderhaft.degraph;

import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;

public class ClasspathBuilder {
    private final DependencyGraphBuilder dependencyGraphBuilder;
    public ClasspathBuilder(DependencyGraphBuilder dependencyGraphBuilder) {
        this.dependencyGraphBuilder = dependencyGraphBuilder;
    }

    public List collectProjectDependencies(MavenSession mavenSession) throws DependencyGraphBuilderException {
        DefaultProjectBuildingRequest request = new DefaultProjectBuildingRequest(mavenSession.getProjectBuildingRequest());
        request.setProject(mavenSession.getCurrentProject());
        request.setRepositorySession(mavenSession.getRepositorySession());
        DependencyNode node = dependencyGraphBuilder.buildDependencyGraph(request,
                AnyArtifact.instance());
        System.err.println(node);
        return Collections.emptyList();
    }
}

