package de.schauderhaft.degraph;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;

public class AnyArtifact implements ArtifactFilter {
    private static final AnyArtifact instance = new AnyArtifact();
    private AnyArtifact() {
    }

    public static AnyArtifact instance() {
        return instance;
    }

    @Override
    public boolean include(Artifact artifact) {
        return true;
    }
}
