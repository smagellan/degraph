package de.schauderhaft.degraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import de.schauderhaft.degraph.configuration.Configuration;
import de.schauderhaft.degraph.configuration.Constraint;
import de.schauderhaft.degraph.configuration.ConstraintViolation;
import de.schauderhaft.degraph.configuration.CycleFree$;
import de.schauderhaft.degraph.configuration.Pattern;
import de.schauderhaft.degraph.configuration.Print;
import de.schauderhaft.degraph.graph.Graph;
import de.schauderhaft.degraph.model.Node;
import de.schauderhaft.degraph.writer.DefaultEdgeStyle$;
import de.schauderhaft.degraph.writer.EdgeStyle;
import de.schauderhaft.degraph.writer.PredicateStyler;
import de.schauderhaft.degraph.writer.SlicePredicate;
import scala.collection.JavaConversions;
import scala.collection.Seq;
import scala.xml.Elem;
import scala.xml.XML;

import java.awt.Color;

public class DegraphJavaAdapter {
    private final DegraphJavaConfig config;
    private final Configuration nativeConfig;
    private Graph graph;
    private List<Seq<Node>> violationNodes;
    private List<ConstraintViolation> violations;
    public DegraphJavaAdapter(DegraphJavaConfig config) {
        this.config = config;
        this.nativeConfig = config.toNativeConfig();
        this.graph = null;
        this.violationNodes = null;
    }

    public void analyze() {
        System.err.println("config: " + nativeConfig);
        System.err.println("creating graph");
        graph = nativeConfig.createGraph();
        System.err.println("done");
        violations = constraintsToViolations(config.getConstraints(), graph);
        System.err.println(violations);
        violationNodes = violationsToNodes();
        System.err.println(violationNodes);
    }

    private void checkAnalyzed() {
        if (graph == null) {
            throw new IllegalStateException("Wrong state: graph not initialized. Please call analyze() first");
        }
    }

    public void storeGraph() {
        checkAnalyzed();
        if (nativeConfig.output() instanceof Print) {
            //here walk dragons: temp var to get rid of type parameters
            scala.Function1 slicing = nativeConfig.slicing();
            //here walk dragons: Idea IDE expects 2-args PredicateStyler.styler
            scala.Function1<scala.Tuple2<Node, Node>, EdgeStyle> styler = PredicateStyler.styler(
                    new SlicePredicate(slicing, ConversionHelper.toImmutableScalaSet(new HashSet(violationNodes))),
                    EdgeStyle.apply(Color.RED, 2.0),
                    DefaultEdgeStyle$.MODULE$);

            System.err.println("processing report xml");
            Elem xml = new de.schauderhaft.degraph.writer.Writer(styler).toXml(graph);
            XML.save(((Print) nativeConfig.output()).path(), xml, "UTF-8", true, null);
            System.err.println("done");
        }
    }

    private List<ConstraintViolation> constraintsToViolations(Set<Constraint> constraints, Graph graph) {
        List<ConstraintViolation> result = new ArrayList<>();
        for (Constraint constraint : constraints) {
            Collection<ConstraintViolation> violations = JavaConversions.asJavaCollection(constraint.violations(graph));
            result.addAll(violations);
        }
        return result;
    }

    private List<Seq<Node>> violationsToNodes() {
        List<Seq<Node>> result = new ArrayList<>();
        for (ConstraintViolation violation : violations) {
            Collection<Seq<Node>> dependencies = DegraphJavaAdapter.violationToDependenciesCollection(violation);
            result.addAll(dependencies);
        }
        return result;
    }

    private static Collection<Seq<Node>> violationToDependenciesCollection(ConstraintViolation violation) {
        //here walk dragons: for some reason Idea IDE is stuck with types here(i.e. expects Tuple2 instead of Seq);
        scala.collection.Seq dependencies = violation.dependencies();
        return JavaConversions.asJavaCollection(dependencies.toList());
    }

    public static void main(String[] args) {
        String classPath = "/home/smagellan/projects/cyclic-dependencies";
        List<String> includes = Arrays.asList();
        List<String> excludes = Arrays.asList();
        Map<String, List<Pattern>> categories = Collections.emptyMap();
        //TODO: introduce type parameter (here walk dragons)
        Set constraint = Collections.singleton(CycleFree$.MODULE$);
        DegraphJavaConfig config = new DegraphJavaConfig(classPath, includes, excludes, categories,
                new Print("/tmp/degraph-test.xml", true), constraint);
        DegraphJavaAdapter adapter = new DegraphJavaAdapter(config);
        adapter.analyze();
        System.err.println(adapter.getViolations().isEmpty());
    }

    public Graph getGraph() {
        checkAnalyzed();
        return graph;
    }

    public List<Seq<Node>> getViolationNodes() {
        checkAnalyzed();
        return Collections.unmodifiableList(violationNodes);
    }

    public List<ConstraintViolation> getViolations() {
        checkAnalyzed();
        return Collections.unmodifiableList(violations);
    }
}
