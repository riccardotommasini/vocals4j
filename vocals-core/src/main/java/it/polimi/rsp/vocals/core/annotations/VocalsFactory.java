package it.polimi.rsp.vocals.core.annotations;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class VocalsFactory {

    protected static VocalsFactory INSTANCE;
    public static final Map<String, String> prefixMap = new HashMap<>();
    protected final String uri_query;
    protected final String qstring;
    protected final String body_query;

    public VocalsFactory(String uri_query, String qstring, String body_query) {
        INSTANCE = this;
        this.uri_query = uri_query;
        this.qstring = qstring;
        this.body_query = body_query;
    }

    public static VocalsFactory get() {
        return INSTANCE;
    }


    public abstract VocalsStub toVocals(final Class<?> engine, final String name);

    public abstract VocalsStreamStub fetch(String uri);

    public abstract List<Endpoint> fromVocals(VocalsStub descr);

}
