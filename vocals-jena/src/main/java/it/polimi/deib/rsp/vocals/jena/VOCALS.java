package it.polimi.deib.rsp.vocals.jena;

import lombok.Getter;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class VOCALS  {

    @Getter
    private static String uri = "http://w3id.org/rsp/vocals#";

    public static Property feature;

    static {
        feature = property("feature");
    }

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(uri + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(uri, local);
    }


}
