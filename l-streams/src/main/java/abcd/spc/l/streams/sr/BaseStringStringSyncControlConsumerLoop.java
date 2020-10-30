package abcd.spc.l.streams.sr;

import java.util.List;

public abstract class BaseStringStringSyncControlConsumerLoop extends BaseSyncControlConsumerLoop<String, String> {
    public BaseStringStringSyncControlConsumerLoop(
            String id, String appId, String clientId, String groupId, String bootstrapServers, List<String> inputTopics) {
        super(id, appId, clientId, groupId, bootstrapServers, inputTopics);
    }

    @Override
    protected String keyDeserializer() {
        return "org.apache.kafka.common.serialization.StringDeserializer";
    }

    @Override
    protected String valueDeserializer() {
        return "org.apache.kafka.common.serialization.StringDeserializer";
    }
}
