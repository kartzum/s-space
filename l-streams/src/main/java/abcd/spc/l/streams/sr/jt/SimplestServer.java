package abcd.spc.l.streams.sr.jt;

import org.eclipse.jetty.server.Server;

/**
 * Simplest Server.
 * <p>
 * https://www.eclipse.org/jetty/documentation/current/embedding-jetty.html
 */
public class SimplestServer {
    public static Server createServer(int port) {
        return new Server(port);
    }

    public static void main(String[] args) throws Exception {
        int port = 8091;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        Server server = createServer(port);
        server.start();
        server.join();
    }
}
