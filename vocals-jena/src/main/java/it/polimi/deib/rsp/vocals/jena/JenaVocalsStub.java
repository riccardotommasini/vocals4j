package it.polimi.deib.rsp.vocals.jena;

import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.rsp.vocals.core.annotations.VocalsStub;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log
@RequiredArgsConstructor
public class JenaVocalsStub implements VocalsStub {

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
        RDFDataMgr.write(out, model, RDFFormat.JSONLD_PRETTY);
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
        Set<Endpoint> set = new HashSet<>();

        Query q = QueryFactory.create(qstring);
        ParameterizedSparqlString parametrized_uri_query = new ParameterizedSparqlString();
        parametrized_uri_query.setCommandText(uri_query);

        ParameterizedSparqlString parametrized_body_query = new ParameterizedSparqlString();
        parametrized_body_query.setCommandText(body_query);

        QueryExecution queryExecution = QueryExecutionFactory.create(q, model);

        ResultSet res = queryExecution.execSelect();

        while (res.hasNext()) {

            List<Endpoint.Par> params = new ArrayList<>();
            QuerySolution s = res.next();

            parametrized_uri_query.setParam("?feature", s.get("?feature"));
            parametrized_body_query.setParam("?feature", s.get("?feature"));

            log.info(parametrized_uri_query.toString());

            Query query = parametrized_uri_query.asQuery();
            ResultSet param = QueryExecutionFactory.create(query, model).execSelect();

            while (param.hasNext()) {
                QuerySolution next = param.next();
                String name = next.get("?name").toString();
                int index = Integer.parseInt(next.get("?index").toString());
                params.add(new Endpoint.Par(name, index, true));
            }

            query = parametrized_body_query.asQuery();
            param = QueryExecutionFactory.create(query, model).execSelect();
            log.info(parametrized_body_query.toString());

            while (param.hasNext()) {
                QuerySolution next = param.next();
                String name = next.get("?name").toString().replace("\\", "");
                int index = Integer.parseInt(next.get("?index").toString());
                log.info(name);
                params.add(new Endpoint.Par(name, index, false));
            }

            params.sort((o1, o2) -> o1.index < o2.index ? -1 : (
                    o1.index == o2.index ? 0 : -1));

            String feature = "";
            if (s.contains("?feature")) {
                feature = s.get("?feature").toString();
            } else if (s.contains("?method") && "GET".equals(s.get("?method").toString())) {
                feature = params.size() > 1 ? ":GetterFeatureN" : ":GetterFeature" + params.size();
            }

            Endpoint.Par[] params1 = new Endpoint.Par[params.size()];

            for (int i = 0; i < params.size(); i++) {
                params1[i] = params.get(i);
            }

            set.add(new Endpoint(
                    s.get("?name").toString(),
                    s.get("?endpoint").toString(),
                    HttpMethod.valueOf(s.get("?method").toString()),
                    feature,
                    params1));
        }

        return this.endpoints = new ArrayList<>(set);

    }
}
