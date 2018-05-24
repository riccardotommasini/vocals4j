package it.polimi.rsp.vocals.core.annotations;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class VocalsFactory {

    protected static VocalsFactory INSTANCE;
    public static final Map<String, String> prefixMap = new HashMap<>();

    public static VocalsFactory get() {
        return INSTANCE;
    }

    public VocalsFactory() {
        INSTANCE = this;
    }


    public abstract VocalsStub toVocals(final Class<?> engine);

    public abstract List<Endpoint> fromVocals(VocalsStub descr);

}
