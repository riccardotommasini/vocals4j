package it.deib.tests.model;

import it.polimi.rsp.vocals.core.annotations.services.ProcessingService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
@ProcessingService(host = "localhost", port = 8181)
public class MockSingles implements StreamRegistrationFeature, StreamsGetterFeature {

    String name, base;

    public List<Obj> get_streams() {
        return null;
    }

    public Obj register_stream(String id, String uri) {
        return null;
    }
}
