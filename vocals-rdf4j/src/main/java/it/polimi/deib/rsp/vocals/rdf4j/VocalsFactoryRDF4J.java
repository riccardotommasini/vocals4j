package it.polimi.deib.rsp.vocals.rdf4j;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.VocalsFactory;
import it.polimi.rsp.vocals.core.annotations.VocalsStreamStub;
import it.polimi.rsp.vocals.core.annotations.VocalsStub;
import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.Param;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;
import it.polimi.rsp.vocals.core.annotations.services.Catalog;
import it.polimi.rsp.vocals.core.annotations.services.ProcessingService;
import it.polimi.rsp.vocals.core.annotations.services.PublishingService;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log
public class VocalsFactoryRDF4J extends VocalsFactory {

    private static final ValueFactory vf = SimpleValueFactory.getInstance();

    public VocalsFactoryRDF4J() throws IOException {
        super(IOUtils.toString(VocalsFactoryRDF4J.class.getClassLoader()
                        .getResourceAsStream("endpoints.sparql"), Charset.defaultCharset()),
                IOUtils.toString(VocalsFactoryRDF4J
                        .class.getClassLoader().getResourceAsStream("uri_params.sparql"), Charset.defaultCharset()),
                IOUtils.toString(VocalsFactoryRDF4J
                        .class.getClassLoader().getResourceAsStream("body.sparql"), Charset.defaultCharset()));
    }


    static {
        prefixMap.put(VOCALS.PREFIX, VOCALS.NAMESPACE);
        prefixMap.put(VSD.PREFIX, VSD.NAMESPACE);
        prefixMap.put(VPROV.PREFIX, VPROV.NAMESPACE);
        prefixMap.put(VOCALS.PREFIX, VOCALS.NAMESPACE);
        prefixMap.put(XMLSchema.PREFIX, XMLSchema.NAMESPACE);
        prefixMap.put("frmt", "http://www.w3.org/ns/formats/");
    }

    public VocalsStub toVocals(final Class<?> engine, String name) {
        ModelBuilder builder = new ModelBuilder();

        prefixMap.entrySet().stream().forEach(e -> builder.setNamespace(e.getKey(), e.getValue()));

        IRI e = getEngineResource(engine, name, builder);

        String engine_base = e.toString();

        Class<?>[] interfaces = engine.getInterfaces();
        Arrays.stream(interfaces)
                //.filter(i -> i.isAnnotationPresent(Feature.class))
                .forEach((Class<?> clazz) -> Arrays.stream(clazz.getMethods())
                        .forEachOrdered(method1 -> {

//                    Class<?> returnType = method.getReturnType();
//                    if (returnType.isAnnotationPresent(Exposed.class)) {
//                        Exposed annotation = returnType.getAnnotation(Exposed.class);
//                        String name = annotation.name();
//                        String endpt = URIUtils.SLASH + name;
//                        createGetterEndpoint(graph, e, name, endpt, "");
//                        Arrays.stream(returnType.getFields())
//                                .filter(field -> field.isAnnotationPresent(Key.class))
//                                .map(Field::getName).forEach(field ->
//                                createGetterEndpoint(graph, e, name, URIUtils.addParam(endpt, field), field));
//                    }
//
//                    if (returnType.isAnnotationPresent(Deletable.class)) {
//                        Deletable del = returnType.getAnnotation(Deletable.class);
//                        String delname = del.name();
//                        String endpt = URIUtils.SLASH + delname;
//                        createDeleteEndpoint(graph, e, delname, endpt, "");
//                        Arrays.stream(returnType.getFields())
//                                .filter(field -> field.isAnnotationPresent(Key.class))
//                                .map(Field::getName).forEach(field ->
//                                createDeleteEndpoint(graph, e, delname, URIUtils.addParam(endpt, field), field));
//                    }

                            BNode service = vf.createBNode();
                            builder.add(e, VSD.hasService, service);

                            Feature feature_annotation =
                                    clazz.isAnnotationPresent(Feature.class) ?
                                            clazz.getAnnotation(Feature.class) :
                                            method1.getAnnotation(Feature.class);

                            RSPService annotation1 = method1.getAnnotation(RSPService.class);
                            String endpointstr = annotation1.endpoint();

                            for (Parameter p : method1.getParameters()) {
                                Param param = p.getAnnotation(Param.class);
                                BNode pbn = vf.createBNode();

                                if (param.uri()) {
                                    endpointstr += "/:" + param.name();
                                    builder.add(service, VSD.uri_param, pbn);
                                    builder.add(pbn, VSD.type, type_selector(p.getType()));
                                    builder.add(pbn, VSD.index, p.getName().replace("arg", ""));

                                } else {
                                    JsonObject serialize = serialize(param, p.getType()).getAsJsonObject();
                                    serialize.entrySet().forEach(entry -> {
                                        BNode bp = vf.createBNode();
                                        builder.add(service, VSD.body_param, bp);
                                        builder.add(bp, VSD.name, entry.getKey());

                                        Resource t = !entry.getValue().isJsonArray() ?
                                                vf.createIRI(entry.getValue().getAsString()) :
                                                vf.createIRI(XMLSchema.NAMESPACE + "#sequence");

                                        builder.add(bp, RDF.TYPE, t);
                                        builder.add(bp, VSD.index, p.getName().replace("arg", ""));

                                    });

                                    builder.add(service, VSD.body, serialize.toString());
                                }

                                builder.add(pbn, VSD.name, param.name());

                            }

                            String ns = "UNKNOWN".equals(feature_annotation.ns()) ? engine_base + "/" : feature_annotation.ns();

                            Resource feat = vf.createIRI(ns, feature_annotation.name());

                            builder.add(service, VOCALS.feature, feat);
                            builder.add(service, VSD.name, feature_annotation.name());
                            builder.add(service, VSD.endpoint, endpointstr);
                            builder.add(service, VSD.method, annotation1.method().name());

                        }));

        return new RDF4JVocalsStub(builder.build());
    }

    @Override
    public VocalsStreamStub fetch(String uri) {

        try {

            String q = IOUtils.toString(VocalsFactoryRDF4J.class.getClassLoader().getResourceAsStream("sgraph.sparql"), Charset.defaultCharset());

            URL url = new URL(uri);

            InputStream inputStream = url.openStream();

            RDFParser rdfParser = Rio.createParser(RDFFormat.JSONLD);

            Model model = new LinkedHashModel();
            rdfParser.setRDFHandler(new StatementCollector(model));
            rdfParser.parse(inputStream, "http://www.example.org/vocals/examples#");

            Repository repo = new SailRepository(new MemoryStore());
            repo.initialize();

            RepositoryConnection con = repo.getConnection();

            model.forEach(con::add);

            TupleQuery tq = con.prepareTupleQuery(QueryLanguage.SPARQL, q);

            TupleQueryResult evaluate = tq.evaluate();

            while (evaluate.hasNext()){
                BindingSet next = evaluate.next();

                String uri2 = next.getValue("stream").stringValue();
                String publisher = next.getValue("service").stringValue();
                String endpoint = next.getValue("endpoint").stringValue();
                String source = next.getValue("url").stringValue();
                String format = next.getValue("format").stringValue();

                return new VocalsStreamStub(uri2, publisher, endpoint, source, format);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private static IRI getEngineResource(Class<?> engine, String name, ModelBuilder model) {
        String uri = "";
        IRI service = null;
        if (engine.isAnnotationPresent(PublishingService.class)) {
            PublishingService cat = engine.getAnnotation(PublishingService.class);
            uri = "http://" + cat.host() + ":" + cat.port();
            service = VSD.PublishingService;

        } else if (engine.isAnnotationPresent(ProcessingService.class)) {
            ProcessingService cat = engine.getAnnotation(ProcessingService.class);
            uri = "http://" + cat.host() + ":" + cat.port();
            service = VSD.ProcessingService;

        }
        if (engine.isAnnotationPresent(Catalog.class)) {
            Catalog cat = engine.getAnnotation(Catalog.class);
            uri = "http://" + cat.host() + ":" + cat.port();
            service = VSD.CatalogService;

        }

        IRI e = vf.createIRI(uri.replace("\\", "") + "/" + name);
        model.defaultGraph().subject(e)
                .add(RDF.TYPE, service)
                .add(VSD.base, uri);

        return e;
    }

    public static JsonElement serialize(Param param, Class<?> c) {
        return serialize(param, new JsonObject(), "", c);
    }

    public static JsonElement serialize(Param param2, JsonElement obj, String name, Class<?> c) {
        if (c.isPrimitive() || String.class.equals(c)) {
            String n = name.isEmpty() ? param2.name() : name;
            if (obj.isJsonObject())
                ((JsonObject) obj).addProperty(n, type_selector(c).stringValue());
        } else if (c.isArray()) {
            if (obj.isJsonObject()) {
                ((JsonObject) obj).add(name, new JsonArray());
            }
        } else {
            Arrays.stream(c.getFields())
                    .forEach(field -> serialize(param2, obj, field.getName(), field.getType()));

            Arrays.stream(c.getMethods())
                    .filter(m -> m.getName().
                            startsWith("set"))
                    .filter(method -> method.getParameterCount() == 1)
                    .forEach(method ->
                            serialize(param2, obj, method.getName().replace("set", ""), method.getParameters()[0].getType()));
        }

        return obj;
    }

    public static IRI type_selector(Class<?> c) {
        if (String.class.equals(c))
            return XMLSchema.STRING;
        else if (Integer.class.equals(c))
            return XMLSchema.INTEGER;
        else if (Long.class.equals(c))
            return XMLSchema.LONG;
        else if (Boolean.class.equals(c))
            return XMLSchema.BOOLEAN;
        else if (Float.class.equals(c))
            return XMLSchema.FLOAT;
        else if (Double.class.equals(c))
            return XMLSchema.DOUBLE;
        else return XMLSchema.STRING;
    }

    public List<Endpoint> fromVocals(VocalsStub stub) {
        try {
            String qstring = IOUtils.toString(VocalsFactoryRDF4J.class.getClassLoader().getResourceAsStream("endpoints.sparql"), Charset.defaultCharset());
            String uri_query = IOUtils.toString(VocalsFactoryRDF4J.class.getClassLoader().getResourceAsStream("uri_params.sparql"), Charset.defaultCharset());
            String body_query = IOUtils.toString(VocalsFactoryRDF4J.class.getClassLoader().getResourceAsStream("body.sparql"), Charset.defaultCharset());

            stub.setQueries(qstring, uri_query, body_query);
            return stub.parse();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

}
