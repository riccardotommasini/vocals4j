package it.deib.tests.vocals.jena;

import it.polimi.deib.rsp.vocals.jena.VocalsFactoryJena;
import it.polimi.rsp.vocals.core.annotations.VocalsStreamStub;
import model.MockDouble;
import model.MockSingles;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static it.polimi.rsp.vocals.core.annotations.VocalsFactory.get;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TestJena {

    @Test
    public void jena() throws IOException {

        VocalsFactoryJena factory = new VocalsFactoryJena();

        String mock1 = get().toVocals(MockDouble.class, "mock").toJsonLD();
        System.out.println(mock1);
        Model mock_double = ModelFactory.createDefaultModel()
                .read(new ByteArrayInputStream(
                        mock1.getBytes(StandardCharsets.UTF_8)), "", "JSON-LD");
        Model mock_singles = ModelFactory.createDefaultModel()
                .read(new ByteArrayInputStream(
                        get().toVocals(MockSingles.class, "mock").toJsonLD().getBytes()), "", "JSON-LD");

        RDFDataMgr.write(System.out, mock_double, RDFFormat.JSONLD_COMPACT_PRETTY);
        RDFDataMgr.write(System.out, mock_singles, RDFFormat.JSONLD_COMPACT_PRETTY);
        assertTrue(mock_double.isIsomorphicWith(mock_singles));

    }

    @Test
    public void sgraph() throws IOException {

        VocalsFactoryJena factory = new VocalsFactoryJena();

        VocalsStreamStub fetch = factory.fetch("http://localhost:4040/sgraph");

        VocalsStreamStub stub = new VocalsStreamStub(
                "http://www.example.org/vocals/stream1",
                "http://www.example.org/vocals/Triplewave",
                "http://www.example.org/vocals/MilanTrafficStreamEndpoint",
                "ws://example.org/traffic/milan",
                "http://www.w3.org/ns/formats/JSON-LD");

        assertEquals(stub, fetch);


    }


}
