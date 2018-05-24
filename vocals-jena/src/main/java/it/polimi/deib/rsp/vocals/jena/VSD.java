package it.polimi.deib.rsp.vocals.jena;

import lombok.Getter;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class VSD {
    @Getter
    private static String uri = "http://w3id.org/rsp/vocals-sd#";

    public final static Property hasService;
    public final static Property name;
    public final static Property uri_param;
    public final static Property body_param;
    public final static Property endpoint;
    public final static Property method;
    public final static Resource ProcessingService;
    public final static Resource CatalogService;
    public final static Resource PublishingService;
    public final static Property base;
    public final static Property params;
    public final static Property index;
    public final static Property body;
    public final static Property type;
    public final static Resource ModelExposure;
    public final static Resource ModelDeletion;

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

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(uri + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(uri, local);
    }

}
