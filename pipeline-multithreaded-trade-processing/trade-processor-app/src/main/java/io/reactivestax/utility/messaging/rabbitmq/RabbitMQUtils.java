package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import io.reactivestax.utility.exceptions.MessageProviderNotSetException;
import io.reactivestax.utility.exceptions.NullResponseForThreadException;
import io.reactivestax.utility.exceptions.RabbitMQException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;

public class RabbitMQUtils {
    private static ConnectionFactory rabbitMQFactory;
    private static Connection rabbitMQConnection;
    private static RabbitMQUtils instance;
    private static final ThreadLocal<Channel> channelThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<GetResponse> getResponseThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<RabbitMQMessageProvider> getMessageProviderThreadLocal = new ThreadLocal<>();

    private RabbitMQUtils() {
    }

    public static synchronized RabbitMQUtils getInstance() {
        if (instance == null) instance = new RabbitMQUtils();
        return instance;
    }

    private static void configureRabbitMQ(String host, String guest, String password) {
        rabbitMQFactory = new ConnectionFactory();
        rabbitMQFactory.setHost(host); // Or the RabbitMQ server IP/hostname
        rabbitMQFactory.setUsername(guest); // RabbitMQ username
        rabbitMQFactory.setPassword(password); // RabbitMQ password
    }

    private static synchronized void getRabbitMQConnection() {
        if (rabbitMQFactory == null)
            configureRabbitMQ(getFileProperty("rabbitMQ.hostName"), getFileProperty("rabbitMQ.guest"), getFileProperty("rabbitMQ.pass"));
        if (rabbitMQConnection == null) {
            try {
                rabbitMQConnection = rabbitMQFactory.newConnection();
            } catch (IOException | TimeoutException e) {
                throw new RabbitMQException(e);
            }
        }
    }

    public void closeRabbitMQConnection() throws IOException {
        rabbitMQConnection.close();
    }

    public Channel getRabbitMQChannel() {
        Channel channel = channelThreadLocal.get();
        if (channel == null) {
            try {
                if (rabbitMQConnection == null) getRabbitMQConnection();
                channel = rabbitMQConnection.createChannel();
                channelThreadLocal.set(channel);
            } catch (Exception e) {
                System.out.println("Unable to provide Channel from the Rabbit MQ Connection...");
                e.printStackTrace();
                throw new RabbitMQException(e);
            }
        }
        return channel;
    }

    public void closeRabbitMQChannel() {
        try {
            if (channelThreadLocal.get() != null) {
                getRabbitMQChannel().close();
                channelThreadLocal.remove();
                getResponseThreadLocal.remove();
            }
        } catch (IOException | TimeoutException e) {
            System.out.println("RabbitMQ Channel already closed!");
        }
    }

    public GetResponse getThreadResponse() {
        GetResponse response = getResponseThreadLocal.get();

        if (response != null) return response;
        else throw new NullResponseForThreadException();
    }

    public void setThreadResponse(GetResponse response) {
        getResponseThreadLocal.set(response);
    }

    public void clearThreadResponse() {
        getResponseThreadLocal.remove();
    }

    public RabbitMQMessageProvider getRabbitMQMessageProvider() {
        RabbitMQMessageProvider rabbitMQMessageProvider = getMessageProviderThreadLocal.get();

        if (rabbitMQMessageProvider != null) return rabbitMQMessageProvider;
        else throw new MessageProviderNotSetException();
    }

    public void setRabbitMQMessageProvider(RabbitMQMessageProvider messageProvider) {
        getMessageProviderThreadLocal.set(messageProvider);
    }

    public void clearRabbitMQMessageProvider() {
        getMessageProviderThreadLocal.remove();
    }
}
