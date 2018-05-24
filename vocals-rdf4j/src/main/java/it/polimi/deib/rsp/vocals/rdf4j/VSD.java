package it.polimi.deib.rsp.vocals.rdf4j;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class VSD {
    public static String NAMESPACE = "http://w3id.org/rsp/vocals-sd#";

    public static final String PREFIX = "vsd";

    private static final ValueFactory vf = SimpleValueFactory.getInstance();

    public final static IRI hasService;
    public final static IRI name;
    public final static IRI uri_param;
    public final static IRI body_param;
    public final static IRI endpoint;
    public final static IRI method;
    public final static IRI ProcessingService;
    public final static IRI CatalogService;
    public final static IRI PublishingService;
    public final static IRI base;
    public final static IRI params;
    public final static IRI index;
    public final static IRI body;
    public final static IRI type;
    public final static IRI ModelExposure;
    public final static IRI ModelDeletion;

    static {
        ProcessingService = resource("ProcessingService");
        CatalogService = resource("CatalogService");
        PublishingService = resource("PublishingService");
        ModelExposure = resource("ModelExposure");
        ModelDeletion = resource("ModelDeletion");
        base = property("base");
        type = property("type");
        hasService = property("hasService");
        name = property("name");
        uri_param = property("uri_param");
        body_param = property("body_param");
        body = property("body");
        endpoint = property("endpoint");
        method = property("method");
        params = property("params");
        index = property("index");
    }

    protected static final IRI resource(String local) {
        return vf.createIRI(NAMESPACE + local);
    }

    protected static final IRI property(String local) {
        return vf.createIRI(NAMESPACE, local);
    }

}
