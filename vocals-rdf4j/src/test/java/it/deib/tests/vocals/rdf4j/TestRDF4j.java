package it.deib.tests.vocals.rdf4j;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import it.polimi.deib.rsp.vocals.rdf4j.VocalsFactoryRDF4J;
import it.polimi.rsp.vocals.core.annotations.VocalsStub;
import model.MockDouble;
import model.MockSingles;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.JSONLDMode;
import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;
import org.eclipse.rdf4j.rio.jsonld.JSONLDWriterFactory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;


public class TestRDF4j {

    @Test
    public void rdf4j() throws IOException {

        VocalsFactoryRDF4J factory = new VocalsFactoryRDF4J();

        VocalsStub vocalsStub = VocalsFactoryRDF4J.get().toVocals(MockDouble.class, "mock1");
        VocalsStub vocalsStub1 = VocalsFactoryRDF4J.get().toVocals(MockSingles.class, "mock1");

        Model mock_double = Rio.parse(new ByteArrayInputStream(vocalsStub.toJsonLD().getBytes()), "", RDFFormat.JSONLD);

        Model mock_singles = Rio.parse(new ByteArrayInputStream(vocalsStub1.toJsonLD().getBytes()), "", RDFFormat.JSONLD);

        JSONLDWriterFactory jsonldWriterFactory = new JSONLDWriterFactory();
        RDFWriter rdfWriter = jsonldWriterFactory.getWriter(System.out);
        VocalsFactoryRDF4J.prefixMap.forEach(rdfWriter::handleNamespace);
        rdfWriter.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
        rdfWriter.startRDF();
        mock_double.forEach(rdfWriter::handleStatement);
        rdfWriter.endRDF();

        RDFWriter rdfWriter2 = jsonldWriterFactory.getWriter(System.err);
        VocalsFactoryRDF4J.prefixMap.forEach(rdfWriter2::handleNamespace);
        rdfWriter2.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
        rdfWriter2.startRDF();
        mock_singles.forEach(rdfWriter2::handleStatement);
        rdfWriter2.endRDF();

        assertTrue(Models.isomorphic(mock_double, mock_singles));
    }

}
