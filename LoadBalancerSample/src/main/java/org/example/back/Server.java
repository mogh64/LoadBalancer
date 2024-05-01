package org.example.back;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Server {
    private String schema ;
    private String host;
    private Integer port;
    private String context;
    private Integer capacity;
    public String getPing() {
        return schema + "://" + host + ":" + port + "/" + context + "/" + "ping";
    }
    public String getAddress() {
        StringBuilder address = new StringBuilder(schema + "://");
        address.append(host);
        if (port!=null) {
           address.append(":").append(port);
        }
        return address.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Server))
            return false;
        Server that = (Server) obj;
        return that.getHost().equals(this.host) && that.getPort().equals(this.port) && that.getContext().equals(this.context);
    }
}
