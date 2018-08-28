package it.polimi.deib.rsp.vocals.jena;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.rsp.vocals.core.annotations.*;
import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.Param;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;
import it.polimi.rsp.vocals.core.annotations.services.Catalog;
import it.polimi.rsp.vocals.core.annotations.services.ProcessingService;
import it.polimi.rsp.vocals.core.annotations.services.PublishingService;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Log
public class VocalsFactoryJena extends VocalsFactory {

    static {
        prefixMap.put("vocals", VOCALS.getUri());
        prefixMap.put("vsd", VSD.getUri());
        prefixMap.put("xsd", XSD.getURI());
        prefixMap.put("frmt", "http://www.w3.org/ns/formats/");
    }

    public VocalsFactoryJena() throws IOException {
        super(IOUtils.toString(VocalsFactoryJena.class.getClassLoader()
                        .getResourceAsStream("endpoints.sparql"), Charset.defaultCharset()),
                IOUtils.toString(VocalsFactoryJena
                        .class.getClassLoader().getResourceAsStream("uri_params.sparql"), Charset.defaultCharset()),
                IOUtils.toString(VocalsFactoryJena
                        .class.getClassLoader().getResourceAsStream("body.sparql"), Charset.defaultCharset()));
    }


    public VocalsStub toVocals(final Class<?> engine, String name) {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefixes(prefixMap);
        Resource e = getEngineResource(engine, name, model);

        String engine_base = e.getNameSpace();

        Random random = new Random(0);

        Class<?>[] interfaces = engine.getInterfaces();
        Arrays.stream(interfaces)
                //.filter(i -> i.isAnnotationPresent(Feature.class))
                .forEach((Class<?> clazz) -> Arrays.stream(clazz.getMethods())
                        .forEachOrdered(method -> {

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

                            Resource service = model.createResource();
                            e.addProperty(VSD.hasService, service);

                            Feature feature_annotation =
                                    clazz.isAnnotationPresent(Feature.class) ?
                                            clazz.getAnnotation(Feature.class) :
                                            method.getAnnotation(Feature.class);

                            RSPService annotation1 = method.getAnnotation(RSPService.class);
                            String endpoint = annotation1.endpoint();

                            for (Parameter p : method.getParameters()) {
                                Param param = p.getAnnotation(Param.class);
                                Resource pbn = model.createResource();

                                if (param.uri()) {
                                    endpoint += "/:" + param.name();
                                    service.addProperty(VSD.uri_param, pbn);
                                    pbn.addProperty(VSD.type, type_selector(p.getType()));
                                    pbn.addProperty(VSD.index, p.getName().replace("arg", ""));
                                } else {
                                    JsonObject serialize = serialize(param, p.getType()).getAsJsonObject();
                                    serialize.entrySet().forEach(entry -> {
                                        Resource bp = model.createResource();
                                        service.addProperty(VSD.body_param, bp);
                                        bp.addProperty(VSD.name, entry.getKey());
                                        Resource t = !entry.getValue().isJsonArray() ?
                                                model.createResource(entry.getValue().getAsString()) :
                                                model.createResource("array");

                                        bp.addProperty(org.apache.jena.vocabulary.RDF.type, t)
                                                .addProperty(VSD.index, p.getName().replace("arg", ""));
                                    });

                                    service.addProperty(VSD.body, serialize.toString());
                                }

                                pbn.addProperty(VSD.name, param.name());
                            }

                            String ns = "UNKNOWN".equals(feature_annotation.ns()) ? engine_base + "/" : feature_annotation.ns();

                            Resource feat = model.createProperty(ns, feature_annotation.name());
                            service.addProperty(VOCALS.feature, feat);
                            service.addProperty(VSD.name, feature_annotation.name());
                            service.addProperty(VSD.endpoint, endpoint);
                            service.addProperty(VSD.method, annotation1.method().name());
                        }));

        return new JenaVocalsStub(model);
    }

    @Override
    public VocalsStreamStub fetch(String uri) {
        try {
            Model sgraph = ModelFactory.createDefaultModel().read(uri, "JSON-LD");
            String q = IOUtils.toString(VocalsFactoryJena.class.getClassLoader().getResourceAsStream("sgraph.sparql"), Charset.defaultCharset());
            Query query = QueryFactory.create(q);
            ResultSet resultSet = QueryExecutionFactory.create(query, sgraph).execSelect();

            while (resultSet.hasNext()) {

                QuerySolution next = resultSet.next();

                String uri2 = next.get("?stream").toString();
                String publisher = next.get("?service").toString();
                String endpoint = next.get("?endpoint").toString();
                String source = next.get("?url").toString();
                String format = next.get("?format").toString();

                return new VocalsStreamStub(uri2, publisher, endpoint, source, format);

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void createDeleteEndpoint(Model m, Resource e, String name, String endpt, String key) {
        Resource rr = m.createResource("d" + endpt);
        e.addProperty(VSD.hasService, rr);
        rr.addProperty(VSD.name, "d" + endpt)
                .addProperty(VSD.endpoint, endpt)
                .addProperty(VSD.method, HttpMethod.DELETE.name())
                .addProperty(VOCALS.feature, VSD.ModelDeletion);

        if (key != null && !key.isEmpty()) {
            Resource p = m.createResource();
            rr.addProperty(VSD.uri_param, p);
            p.addProperty(VSD.name, key).addProperty(VSD.index, "0");
        }
    }

    private static void createGetterEndpoint(Model m, Resource e, String name, String endpt, String key) {
        Resource rr = m.createResource(endpt);
        e.addProperty(VSD.hasService, rr);
        rr.addProperty(VSD.name, endpt)
                .addProperty(VSD.endpoint, endpt)
                .addProperty(VSD.method, HttpMethod.GET.name())
                .addProperty(VOCALS.feature, VSD.ModelExposure);
        if (key != null && !key.isEmpty()) {
            Resource p = m.createResource();
            rr.addProperty(VSD.uri_param, p);
            p.addProperty(VSD.name, key).addProperty(VSD.index, "0");
        }
    }


    private static Resource getEngineResource(Class<?> engine, String name, Model model) {
        String uri = "";
        Resource service = null;
        if (engine.isAnnotationPresent(PublishingService.class)) {
            PublishingService cat = engine.getAnnotation(PublishingService.class);
            uri = ("http://" + cat.host() + ":" + cat.port()).replace("\\", "") + "/" + name;
            service = VSD.PublishingService;

        } else if (engine.isAnnotationPresent(ProcessingService.class)) {
            ProcessingService cat = engine.getAnnotation(ProcessingService.class);
            uri = ("http://" + cat.host() + ":" + cat.port()).replace("\\", "") + "/" + name;
            service = VSD.ProcessingService;
        } else if (engine.isAnnotationPresent(Catalog.class)) {
            Catalog cat = engine.getAnnotation(Catalog.class);
            uri = ("http://" + cat.host() + ":" + cat.port()).replace("\\", "") + "/" + name;
            service = VSD.CatalogService;

        } else {
            return model.createResource();
        }

        return model.createResource(uri)
                .addProperty(org.apache.jena.vocabulary.RDF.type, service)
                .addProperty(VSD.base, uri);

    }

    public static JsonElement serialize(Param param, Class<?> c) {
        return serialize(param, new JsonObject(), "", c);
    }

    public static JsonElement serialize(Param param2, JsonElement obj, String name, Class<?> c) {
        if (c.isPrimitive() || String.class.equals(c)) {
            String n = name.isEmpty() ? param2.name() : name;
            if (obj.isJsonObject())
                ((JsonObject) obj).addProperty(n, type_selector(c).getURI());
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

    public static Resource type_selector(Class<?> c) {
        if (String.class.equals(c))
            return XSD.xstring;
        else if (Integer.class.equals(c))
            return XSD.integer;
        else if (Long.class.equals(c))
            return XSD.xlong;
        else if (Boolean.class.equals(c))
            return XSD.xboolean;
        else if (Float.class.equals(c))
            return XSD.xfloat;
        else if (Double.class.equals(c))
            return XSD.xdouble;
        else return XSD.xstring;
    }

    public List<Endpoint> fromVocals(VocalsStub stub) {
        try {
            String qstring = IOUtils.toString(VocalsFactoryJena.class.getClassLoader().getResourceAsStream("endpoints.sparql"), Charset.defaultCharset());
            String uri_query = IOUtils.toString(VocalsFactoryJena.class.getClassLoader().getResourceAsStream("uri_params.sparql"), Charset.defaultCharset());
            String body_query = IOUtils.toString(VocalsFactoryJena.class.getClassLoader().getResourceAsStream("body.sparql"), Charset.defaultCharset());

            stub.setQueries(qstring, uri_query, body_query);
            return stub.parse();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }


}
