package de.schauderhaft.degraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    public DegraphJavaAdapter(DegraphJavaConfig config) {
        this.config = config;
    }

    public void analyze() {
        Configuration nativeConfig = config.toNativeConfig();

        System.err.println("config: " + nativeConfig);
        Graph graph = nativeConfig.createGraph();
        List<Seq<Node>> violations = constraintsToNodes(config.getConstraints(), graph);
        System.err.print(violations);
        //here walk dragons: temp var to get rid of type parameters
        scala.Function1 slicing = nativeConfig.slicing();

        //here walk dragons: Idea IDE expects 2-args PredicateStyler.styler
        scala.Function1<scala.Tuple2<Node, Node>, EdgeStyle> styler = PredicateStyler.styler(
                new SlicePredicate(slicing, ConversionHelper.toImmutableScalaSet(new HashSet(violations))),
                EdgeStyle.apply(Color.RED, 2.0),
                DefaultEdgeStyle$.MODULE$);

        Elem xml = new de.schauderhaft.degraph.writer.Writer(styler).toXml(graph);
        XML.save(((Print)nativeConfig.output()).path(), xml, "UTF-8", true, null);
    }

    private List<Seq<Node>> constraintsToNodes(Set<Constraint> constraints, Graph graph) {
        //Collection<ConstraintViolation> violations = new ArrayList<>();
        List<Seq<Node>> result = new ArrayList<>();
        for (Constraint constraint : constraints) {
            Collection<ConstraintViolation> violations = JavaConversions.asJavaCollection(constraint.violations(graph));
            for (ConstraintViolation violation : violations) {
                Collection<Seq<Node>> dependencies = DegraphJavaAdapter.violationToDependenciesCollection(violation);
                result.addAll(dependencies);
            }
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
    }
}
