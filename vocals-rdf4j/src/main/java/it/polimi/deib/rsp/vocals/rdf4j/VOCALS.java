package it.polimi.deib.rsp.vocals.rdf4j;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class VOCALS {

    private static final ValueFactory vf = SimpleValueFactory.getInstance();

    public static String NAMESPACE = "http://w3id.org/rsp/vocals#";
    public static String PREFIX = "vocals";

    public static IRI feature;

    static {
        feature = property("feature");
    }

    protected static final IRI resource(String local) {
        return vf.createIRI(NAMESPACE + local);
    }

    protected static final IRI property(String local) {
        return vf.createIRI(NAMESPACE, local);
    }

}
