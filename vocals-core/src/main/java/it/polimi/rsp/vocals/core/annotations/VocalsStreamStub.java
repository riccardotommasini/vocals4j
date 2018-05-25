package it.polimi.rsp.vocals.core.annotations;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class VocalsStreamStub {

    public final String uri;
    public final String publisher;
    public final String endpoint;
    public final String source;
    public final String format;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VocalsStreamStub that = (VocalsStreamStub) o;

        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        if (publisher != null ? !publisher.equals(that.publisher) : that.publisher != null) return false;
        if (endpoint != null ? !endpoint.equals(that.endpoint) : that.endpoint != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        return format != null ? format.equals(that.format) : that.format == null;
    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (publisher != null ? publisher.hashCode() : 0);
        result = 31 * result + (endpoint != null ? endpoint.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VocalsStreamStub {" +
                "uri='" + uri + '\'' +
                ", publisher='" + publisher + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", source='" + source + '\'' +
                ", format='" + format + '\'' +
                '}';
    }


}
