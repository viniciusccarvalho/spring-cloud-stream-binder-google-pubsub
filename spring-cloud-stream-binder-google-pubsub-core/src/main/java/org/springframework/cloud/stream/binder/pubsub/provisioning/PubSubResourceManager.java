/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.binder.pubsub.provisioning;


import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;

import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.pubsub.properties.PubSubExtendedBindingProperties;
import org.springframework.cloud.stream.binder.pubsub.properties.PubSubProducerProperties;
import org.springframework.util.StringUtils;

/**
 * @author Vinicius Carvalho
 *
 * Utility class to manage PubSub resources. Deals with topic and subscription creation
 * and Future conversion
 *
 * Uses Spring Cloud Stream properties to determine the need to create partitions and
 * consumer groups
 */
public class PubSubResourceManager {

	private PubsubConnection client;

	private PubSubExtendedBindingProperties properties;
	
	public PubSubResourceManager(PubsubConnection client, PubSubExtendedBindingProperties properties) {
		this.client = client;
		this.properties = properties;
	}

	/**
	 * Create a list of topics using name and properties such as prefix and partitionCount.
	 * This binder creates on topic per partition, so if producerProperties.getPartitionCount > 0,
	 * more than one topic will be create for a given destination.
	 * For example, if name = bar, producerProperties.getExtension.getPrefix() = foo,
	 * producerProperties.getPartitionCount = 2 the following topics
	 * would be created: foo.bar-0, foo-bar-1
	 * @param name - The name of the destination
	 * @param producerProperties - Extended producer properties
	 * @return list of topics
	 */
	public List<Topic> createTopic(String name, ExtendedProducerProperties<PubSubProducerProperties> producerProperties){
		Integer partitionIndex = null;
		List<Topic> topics = new LinkedList<>();
		if(producerProperties.isPartitioned()){
			for(int i=0;i<producerProperties.getPartitionCount();i++){
				partitionIndex = i;
				topics.add(declareTopic(name,producerProperties,partitionIndex));
			}
		}else{
			topics.add(declareTopic(name,producerProperties,partitionIndex));
		}
		return topics;
	}

	/**
	 * Create a list of {@link Subscription} based on the required groups settings. For each {@link Topic} used as an input
	 * a set of Subscription will be created based on producerProperties.getRequiredGroups.
	 * So if producerProperties.getRequiredGroups = ["sensor","average"] and the list of topics is ["foo.bar-0" and "foo-bar-1"]
	 * the following subscriptions would be created: ["foo.bar-0.sensor","foo.bar-0.average","foo.bar-1.sensor","foo.bar-1.average"]
	 *
	 * @param topics - List of topics that subscriptions needs to be created
	 * @param producerProperties - Extended producer properties
	 * @return - list of subscriptions
	 */
	public List<Subscription> createRequiredGroups(List<Topic> topics, ExtendedProducerProperties<PubSubProducerProperties> producerProperties){
		List<Subscription> subscriptions = new LinkedList<>();
		for(Topic topic : topics){
			for(String group : producerProperties.getRequiredGroups()){
				subscriptions.add(declareSubscription(topic, topic.getNameAsTopicName().getTopic(),group));
			}
		}
		return subscriptions;
	}

	/**
	 * Creates a topic name using the format ({prefix})?.{name}(-{partitionIndex})?
	 * @param name Name of the destination
	 * @param prefix Prefix to be used, null or empty if none
	 * @param partitionIndex Partition index, null if no partition
	 * @return formatted topic name
	 */
	public String createTopicName(String name, String prefix, Integer partitionIndex) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(applyPrefix(prefix, name));

		if (partitionIndex != null) {
			buffer.append(PubSubBinderOptions.PARTITION_DELIMITER + partitionIndex);
		}
		return buffer.toString();
	}

	public void deleteTopic(String name){
		TopicName topicName = TopicName.create(properties.getBinder().getProjectName(),name);
		this.client.getPublisherClient().deleteTopic(topicName);
	}

	public void deleteSubscription(String name){
		SubscriptionName subscriptionName = SubscriptionName.create(properties.getBinder().getProjectName(),name);
		this.client.getSubscriberClient().deleteSubscription(subscriptionName);
	}

	public String applyPrefix(String prefix, String name) {
		if (StringUtils.isEmpty(prefix))
			return name;
		return prefix + PubSubBinderOptions.GROUP_INDEX_DELIMITER + name;
	}

	private Topic declareTopic(String name, ExtendedProducerProperties<PubSubProducerProperties> producerProperties, Integer partitionIndex){
		TopicName topicName = TopicName.create(this.properties.getBinder().getProjectName(),createTopicName(name,producerProperties.getExtension().getPrefix(),partitionIndex));
		Topic topic = null;
		try{
			topic = this.client.getPublisherClient().createTopic(topicName);
		}catch (Exception e){
			topic = this.client.getPublisherClient().getTopic(topicName);
		}
		return topic;
	}

	private Subscription declareSubscription(Topic topic, String name, String group){
		Subscription subscription = null;
		SubscriptionName subscriptionName = SubscriptionName.create(this.properties.getBinder().getProjectName(),createSubscriptionName(name,group));
		try{

			subscription = this.client.getSubscriberClient().createSubscription(subscriptionName,topic.getNameAsTopicName(), PushConfig.getDefaultInstance(),10);
		}catch (Exception e){
			subscription = this.client.getSubscriberClient().getSubscription(subscriptionName);
		}
		return subscription;
	}

	private String createSubscriptionName(String name, String group) {
		boolean anonymousConsumer = !StringUtils.hasText(group);
		StringBuffer buffer = new StringBuffer();
		if (anonymousConsumer) {
			buffer.append(groupedName(name, UUID.randomUUID().toString()));
		}
		else {
			buffer.append(groupedName(name, group));
		}
		return buffer.toString();
	}

	private final String groupedName(String name, String group) {
		return name + PubSubBinderOptions.GROUP_INDEX_DELIMITER
				+ (StringUtils.hasText(group) ? group : "default");
	}
}
