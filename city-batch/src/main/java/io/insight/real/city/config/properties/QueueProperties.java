package io.insight.real.city.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq")
@Getter @Setter
public class QueueProperties {
    private Population population;
    private City city;
    private Ranking ranking;
    private DeadLetter deadLetter;
    private Employment employment;
    private District district;

    @Getter @Setter
    public static class Population{
        private String exchangeName;
        private String queueName;
        private String routingKey;
        private boolean durable;
        private boolean exclusive;
        private boolean autoDelete;
    }
    @Getter @Setter
    public static class Employment {
        private String exchangeName;
        private String queueName;
        private String routingKey;
        private boolean durable;
        private boolean exclusive;
        private boolean autoDelete;
    }

    @Getter @Setter
    public static class City {
        private String exchangeName;
        private String queueName;
        private String routingKey;
        private boolean durable;
        private boolean exclusive;
        private boolean autoDelete;
    }

    @Getter @Setter
    public static class District {
        private String exchangeName;
        private String queueName;
        private String routingKey;
        private boolean durable;
        private boolean exclusive;
        private boolean autoDelete;
    }

    @Getter @Setter
    public static class Ranking {
        private String exchangeName;
        private boolean durable;
        private boolean exclusive;
        private boolean autoDelete;
        private City city;
        private Population population;

        @Getter @Setter
        public static class City {
            private String queueName;
            private String routingKey;
        }
        @Getter @Setter
        public static class Population {
            private String queueName;
            private String routingKey;
        }
    }

    @Getter @Setter
    public static class DeadLetter {
        private String exchangeName;
        private String queueName;
        private String routingKey;
        private boolean durable;
        public String getExchangeHeaderName(){
            return "x-dead-letter-exchange";
        }
        public String getRoutingHeaderName(){
            return "x-dead-letter-routing-key";
        }
    }
}
