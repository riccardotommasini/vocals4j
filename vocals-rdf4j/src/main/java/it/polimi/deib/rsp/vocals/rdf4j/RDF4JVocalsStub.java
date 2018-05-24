package it.polimi.deib.rsp.vocals.rdf4j;

import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.rsp.vocals.core.annotations.VocalsStub;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.helpers.JSONLDMode;
import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;
import org.eclipse.rdf4j.rio.jsonld.JSONLDWriterFactory;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log
@RequiredArgsConstructor
public class RDF4JVocalsStub implements VocalsStub {

    @Getter
    private final Model model;
    private String qstring;
    private String uri_query;
    private String body_query;
    @Getter
    private List<Endpoint> endpoints;

    @Override
    public String toString() {

        StringWriter out = new StringWriter();

        JSONLDWriterFactory jsonldWriterFactory = new JSONLDWriterFactory();
        RDFWriter rdfWriter = jsonldWriterFactory.getWriter(out);
        VocalsFactoryRDF4J.prefixMap.forEach(rdfWriter::handleNamespace);
        rdfWriter.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
        rdfWriter.startRDF();
        model.forEach(rdfWriter::handleStatement);
        rdfWriter.endRDF();

        return out.toString();
    }

    @Override
    public String toJsonLD() {
        return toString();
    }

    @Override
    public void setQueries(String... queries) {
        if (queries != null && queries.length == 3) {
            this.qstring = queries[0];
            this.uri_query = queries[1];
            this.body_query = queries[2];
        }
    }

    @Override
    public List<Endpoint> parse() {

        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();

        RepositoryConnection con = repo.getConnection();
        model.forEach(con::add);

        Set<Endpoint> set = new HashSet<>();

        TupleQuery q = con.prepareTupleQuery(QueryLanguage.SPARQL, qstring);

        TupleQuery parametrized_uri_query = con.prepareTupleQuery(QueryLanguage.SPARQL, uri_query);

        TupleQuery parametrized_body_query = con.prepareTupleQuery(QueryLanguage.SPARQL, body_query);

        TupleQueryResult res = q.evaluate();

        while (res.hasNext()) {

            List<Endpoint.Par> params = new ArrayList<>();

            BindingSet s = res.next();
            Value feature = s.getValue("feature");

            parametrized_uri_query.setBinding("feature", feature);
            parametrized_body_query.setBinding("feature", feature);

            log.info(parametrized_uri_query.toString());

            TupleQueryResult param = parametrized_uri_query.evaluate();

            while (param.hasNext()) {
                BindingSet next = param.next();
                String name = next.getValue("name").stringValue();
                int index = Integer.parseInt(next.getValue("index").stringValue());
                params.add(new Endpoint.Par(name, index, true));
            }

            param = parametrized_body_query.evaluate();

            log.info(parametrized_body_query.toString());

            while (param.hasNext()) {
                BindingSet next = param.next();
                String name = next.getValue("name").stringValue().replace("\\", "");
                int index = Integer.parseInt(next.getValue("?index").stringValue());
                log.info(name);
                params.add(new Endpoint.Par(name, index, false));
            }

            params.sort((o1, o2) -> o1.index < o2.index ? -1 : (
                    o1.index == o2.index ? 0 : -1));

            String features = "";
            if (s.hasBinding("feature")) {
                features = s.getValue("?feature").stringValue();
            } else if (s.hasBinding("method") && "GET".equals(s.getValue("method").stringValue())) {
                features = params.size() > 1 ? ":GetterFeatureN" : ":GetterFeature" + params.size();
            }

            Endpoint.Par[] params1 = new Endpoint.Par[params.size()];

            for (int i = 0; i < params.size(); i++) {
                params1[i] = params.get(i);
            }

            set.add(new Endpoint(
                    s.getValue("name").stringValue(),
                    s.getValue("endpoint").stringValue(),
                    HttpMethod.valueOf(s.getValue("method").stringValue()),
                    features,
                    params1));
        }

        return new ArrayList<>(set);
    }
}
