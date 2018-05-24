package it.polimi.deib.rsp.vocals.rdf4j;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class VPROV {


    public static final String PREFIX = "vprov";

    public static String NAMESPACE = "http://w3id.org/rsp/vocals-prov#";
    private static final ValueFactory vf = SimpleValueFactory.getInstance();

    protected static final IRI resource(String local) {
        return vf.createIRI(NAMESPACE + local);
    }

    protected static final IRI property(String local) {
        return vf.createIRI(NAMESPACE, local);
    }

}
