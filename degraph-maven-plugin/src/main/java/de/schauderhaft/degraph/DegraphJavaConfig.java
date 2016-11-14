package de.schauderhaft.degraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.schauderhaft.degraph.analysis.AnalyzerLike;
import de.schauderhaft.degraph.analysis.asm.Analyzer$;
import de.schauderhaft.degraph.configuration.Configuration;
import de.schauderhaft.degraph.configuration.Constraint;
import de.schauderhaft.degraph.configuration.CycleFree$;
import de.schauderhaft.degraph.configuration.Pattern;
import de.schauderhaft.degraph.configuration.PrintConfiguration;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Seq;

public class DegraphJavaConfig {
    private final String classpath;
    private final List<String> includes;
    private final List<String> excludes;
    private final Map<String, List<Pattern>> categories;
    private final PrintConfiguration output;
    private final Set<Constraint> constraints;
    private final AnalyzerLike analyzer;
    public DegraphJavaConfig(String classpath, List<String> includes, List<String> excludes,
            Map<String, List<Pattern>> categories, PrintConfiguration output,
            Set<Constraint> constraints, AnalyzerLike analyzer) {
        this.classpath = classpath;
        this.includes = new ArrayList<>(includes);
        this.excludes = new ArrayList<>(excludes);
        this.categories = new HashMap<>();
        this.categories.putAll(categories);
        this.output = output;
        this.constraints = new HashSet<>(constraints);
        this.analyzer = analyzer;
    }

    public DegraphJavaConfig(String classpath, List<String> includes, List<String> excludes,
            Map<String, List<Pattern>> categories, PrintConfiguration output,
            Set<Constraint> constraints) {
        this(classpath, includes, excludes, categories, output, constraints, Analyzer$.MODULE$);
    }

    public String getClasspath() {
        return classpath;
    }

    public List<String> getIncludes() {
        return Collections.unmodifiableList(includes);
    }

    public List<String> getExcludes() {
        return Collections.unmodifiableList(excludes);
    }

    public Map<String, List<Pattern>> getCategories() {
        return Collections.unmodifiableMap(categories);
    }

    public PrintConfiguration getOutput() {
        return output;
    }

    public Set<Constraint> getConstraints() {
        return Collections.unmodifiableSet(constraints);
    }

    public Configuration toNativeConfig() {
        Map<String, Seq<Pattern>> transformedCategories = transofrmedCategories();
        return new Configuration(Option.apply(classpath),
                JavaConversions.asScalaBuffer(includes),
                JavaConversions.asScalaBuffer(excludes),
                ConversionHelper.toImmutableScalaMap(transformedCategories),
                output,
                ConversionHelper.toImmutableScalaSet(getConstraints()),
                analyzer);
    }

    private Map<String, Seq<Pattern>> transofrmedCategories() {
        Map<String, Seq<Pattern>> result = new HashMap<>();
        for (Map.Entry<String, List<Pattern>> entry : categories.entrySet()) {
            String key = entry.getKey();
            Seq<Pattern> value = JavaConversions.asScalaBuffer(entry.getValue());
            result.put(key, value);
        }
        return result;
    }
}
