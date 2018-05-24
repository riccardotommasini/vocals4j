package it.polimi.rsp.vocals.core.annotations;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class Endpoint {

    public final String name, uri;
    public final HttpMethod method;
    public final String feature;
    public final Par[] params;

    public Endpoint(String name, String feature, Par[] params) {
        this.name = name;
        this.feature = feature;
        this.method = HttpMethod.GET;
        this.params = params;
        this.uri = name;
    }

    public Endpoint(String name, String feature) {
        this(name, feature, new Endpoint.Par[]{});
    }


    public static class Par {
        public final int index;

        public Par(String name, int index, boolean uri, Class<?> type) {
            this.index = index;
            this.name = name;
            this.uri = uri;
            this.type = type;
        }

        public final String name;
        public final boolean uri;
        public Class<?> type;

        public Par(String name, int index, boolean uri) {
            this.index = index;
            this.name = name;
            this.uri = uri;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return (((Endpoint) o).method.equals(method) && ((Endpoint) o).uri.equals(uri));
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }
}
