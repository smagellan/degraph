package de.schauderhaft.degraph;

import scala.Predef$;
import scala.collection.JavaConverters;

class ConversionHelper {
    public static <A, B> scala.collection.immutable.Map<A, B> toImmutableScalaMap(java.util.Map<A, B> m) {
        return JavaConverters.mapAsScalaMapConverter(m).asScala().toMap(Predef$.MODULE$.<scala.Tuple2<A, B>>$conforms());
    }

    public static <A> scala.collection.immutable.Set<A> toImmutableScalaSet(java.util.Set<A> s) {
        return JavaConverters.asScalaSetConverter(s).asScala().toSet();
    }
}
