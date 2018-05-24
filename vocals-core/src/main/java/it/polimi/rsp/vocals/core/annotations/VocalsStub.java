package it.polimi.rsp.vocals.core.annotations;

import java.util.List;

public interface VocalsStub {

    String toJsonLD();

    void setQueries(String... queries);

    List<Endpoint> parse();

}
