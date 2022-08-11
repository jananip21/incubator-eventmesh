package org.apache.eventmesh.runtime.core.plugin;

import org.apache.eventmesh.api.admin.Admin;
import org.apache.eventmesh.api.admin.TopicProperties;
import org.apache.eventmesh.api.factory.ConnectorPluginFactory;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.CloudEvent;

public class MQAdminWrapper extends MQWrapper {

    public Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Admin meshMQAdmin;

    public MQAdminWrapper(String connectorPluginType) {
        this.meshMQAdmin = ConnectorPluginFactory.getMeshMQAdmin(connectorPluginType);
        if (meshMQAdmin == null) {
            logger.error("can't load the meshMQAdmin plugin, please check.");
            throw new RuntimeException("doesn't load the meshMQAdmin plugin, please check.");
        }
    }

    public synchronized void init(Properties keyValue) throws Exception {
        if (inited.get()) {
            return;
        }

        meshMQAdmin.init(keyValue);
        inited.compareAndSet(false, true);
    }

    public synchronized void start() throws Exception {
        if (started.get()) {
            return;
        }

        meshMQAdmin.start();

        started.compareAndSet(false, true);
    }

    public synchronized void shutdown() throws Exception {
        if (!inited.get()) {
            return;
        }

        if (!started.get()) {
            return;
        }

        meshMQAdmin.shutdown();

        inited.compareAndSet(true, false);
        started.compareAndSet(true, false);
    }

    public Admin getMeshMQAdmin() {
        return meshMQAdmin;
    }

    public List<TopicProperties> getTopic() throws Exception {
        return meshMQAdmin.getTopic();
    }

    public void createTopic(String topicName) throws Exception {
        meshMQAdmin.createTopic(topicName);
    }

    public void deleteTopic(String topicName) throws Exception {
        meshMQAdmin.deleteTopic(topicName);
    }

    public List<CloudEvent> getEvent(String topicName, int offset, int length) throws Exception {
        return meshMQAdmin.getEvent(topicName, offset, length);
    }

    public void publish(CloudEvent cloudEvent) throws Exception {
        meshMQAdmin.publish(cloudEvent);
    }
}