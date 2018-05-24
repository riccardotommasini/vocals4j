package it.deib.tests.model;

import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.Param;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;

import java.util.List;

public interface StreamRegistrationAndStreamsGetterFeatures {


    @Feature(name = "StreamsGetterFeature")
    @RSPService(endpoint = "/streams")
    List<Obj> get_streams();

    @Feature(name = "StreamRegistrationFeature")
    @RSPService(endpoint = "/streams", method = HttpMethod.POST)
    Obj register_stream(@Param(name = "stream", uri = true) String id,
                        @Param(name = "uri") String uri);

}
