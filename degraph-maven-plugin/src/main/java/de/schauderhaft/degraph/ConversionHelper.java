package de.schauderhaft.degraph;

import scala.Predef$;
import scala.collection.JavaConverters;

class ConversionHelper {
    public static <A, B> scala.collection.immutable.Map<A, B> toScalaMap(java.util.Map<A, B> m) {
        return JavaConverters.mapAsScalaMapConverter(m).asScala().toMap(Predef$.MODULE$.$conforms());
    }

    public static <A> scala.collection.immutable.Set<A> toScalaSet(java.util.Set<A> s) {
        return JavaConverters.asScalaSetConverter(s).asScala().toSet();
    }
}