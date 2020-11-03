package abcd.spc.l.streams.sr.jt;

import abcd.spc.l.streams.sr.base.MicroserviceUtils;
import abcd.spc.l.streams.sr.base.Service;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.server.ManagedAsync;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import static abcd.spc.l.streams.sr.base.MicroserviceUtils.addShutdownHookAndBlock;

/**
 * https://github.com/confluentinc/kafka-streams-examples/blob/6.0.0-post/src/main/java/io/confluent/examples/streams/microservices/OrdersService.java
 * <pre>
 * {@code
 * http://localhost:8098/v1/blocks/10
 * }
 * </pre>
 */
@Path("v1")
public class BlocksServer implements Service {
    private static final String CALL_TIMEOUT = "10000";

    private Server jettyServer;

    @GET
    @ManagedAsync
    @Path("/blocks/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public void getWithTimeout(@PathParam("id") String id,
                               @QueryParam("timeout") @DefaultValue(CALL_TIMEOUT) Long timeout,
                               @Suspended AsyncResponse asyncResponse) {
        fetchLocal(id, asyncResponse);
    }

    private void fetchLocal(String id, AsyncResponse asyncResponse) {
        Block block = new Block(id, id);
        asyncResponse.resume(toBean(block));
    }

    private Block toBean(Block block) {
        return block;
    }

    @Override
    public void start() {
        jettyServer = MicroserviceUtils.startJetty(8098, this);
    }

    @Override
    public void stop() {
        if (jettyServer != null) {
            try {
                jettyServer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Service service = new BlocksServer();
        service.start();
        addShutdownHookAndBlock(service);
    }
}
