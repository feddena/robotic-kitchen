package pizzapipeline.server.database;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import pizzapipeline.server.device.Device;
import pizzapipeline.server.kitchen.Kitchen;

@Component
public class DeviceManager {
    private final static Logger log = LoggerFactory.getLogger(DeviceManager.class);

    private final RedisPublisherImpl redisPublisher;

    protected final static Set<Device> devicesRegistered = new HashSet<>();

    @Autowired
    public DeviceManager(RedisPublisherImpl redisPublisher) {
        this.redisPublisher = redisPublisher;

    }

    public void register(Device device) {
        devicesRegistered.add(device);
    }

    public void applyAction(Device device, String actionDescription, Kitchen.OnSuccessActionJob onSuccessActionJob) {
        device.setOnSuccessActionJob(onSuccessActionJob);
        // it's just sending message to itself but it easily can be edited to receiving message by 'real' device and sending response to this channel
        redisPublisher.publish(device.getName() + " " + actionDescription);
    }

    public static class RedisMessageListener implements MessageListener {
        @Override
        public void onMessage(final Message message, final byte[] pattern) {
            String messageStr = message.toString();
            String[] deviceNameAndActionType = messageStr.split(" ");
            for (Device device : devicesRegistered) {
                if (device.getName().equals(deviceNameAndActionType[0])) {
                    log.debug("completing job for {}", device.getName());
                    device.getOnSuccessActionJob().completeJob(messageStr);
                    break;
                }
            }
        }
    }

    public static class RedisPublisherImpl {
        private final RedisTemplate<String, Object> template;
        private final ChannelTopic topic;

        public RedisPublisherImpl(final RedisTemplate<String, Object> template,
                                  final ChannelTopic topic) {
            this.template = template;
            this.topic = topic;
        }

        public void publish(String message) {
            template.convertAndSend(topic.getTopic(), message);
        }
    }
}
