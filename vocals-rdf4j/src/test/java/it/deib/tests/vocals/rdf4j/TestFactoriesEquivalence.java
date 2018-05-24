package it.deib.tests.vocals.rdf4j;

import it.polimi.deib.rsp.vocals.rdf4j.VocalsFactoryRDF4J;
import model.MockDouble;
import model.MockSingles;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.helpers.JSONLDMode;
import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;
import org.eclipse.rdf4j.rio.jsonld.JSONLDWriterFactory;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;


public class TestFactoriesEquivalence {

    @Test
    public void rdf4j() {

        org.eclipse.rdf4j.model.Model mock_double = (Model) VocalsFactoryRDF4J.get().toVocals(MockDouble.class);
        org.eclipse.rdf4j.model.Model mock_singles = (Model) VocalsFactoryRDF4J.get().toVocals(MockSingles.class);

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
