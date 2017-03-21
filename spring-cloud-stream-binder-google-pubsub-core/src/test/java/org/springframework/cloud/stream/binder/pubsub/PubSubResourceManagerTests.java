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

package org.springframework.cloud.stream.binder.pubsub;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.pubsub.properties.PubSubExtendedBindingProperties;
import org.springframework.cloud.stream.binder.pubsub.properties.PubSubProducerProperties;
import org.springframework.cloud.stream.binder.pubsub.provisioning.PubSubResourceManager;
import org.springframework.cloud.stream.binder.pubsub.provisioning.PubsubConnection;
import org.springframework.cloud.stream.binder.test.junit.pubsub.PubSubTestSupport;

/**
 * @author Vinicius Carvalho
 */
public class PubSubResourceManagerTests{

	@Rule
	public PubSubTestSupport pubsubSupport = new PubSubTestSupport();

	private PubSubResourceManager resourceManager;

	private Set<Topic> topics = new HashSet<>();
	private Set<Subscription> subscriptions = new HashSet<>();

	@Before
	public void initialize(){
		PubSubExtendedBindingProperties properties = new PubSubExtendedBindingProperties();
		properties.getBinder().setProjectName(pubsubSupport.getProjectId());

		this.resourceManager = new PubSubResourceManager(
						new PubsubConnection(pubsubSupport.getPublisherClient(),pubsubSupport.getSubscriberClient())
						,properties);
	}

	@After
	public void clean(){
		for(Topic topic : topics){
			this.resourceManager.deleteTopic(topic.getNameAsTopicName().getTopic());
		}
		this.topics.clear();
		for(Subscription subscription : subscriptions){
			this.resourceManager.deleteSubscription(subscription.getNameAsSubscriptionName().getSubscription());
		}
		this.subscriptions.clear();
	}

	@Test
	public void testNoPrefixNoPartition() throws Exception{
		ExtendedProducerProperties<PubSubProducerProperties> producerProperties = new ExtendedProducerProperties<>(new PubSubProducerProperties());
		List<Topic> topics = this.resourceManager.createTopic("foo",producerProperties);
		this.topics.addAll(topics);
		Assert.assertEquals(1, topics.size());
		Assert.assertEquals("foo",topics.get(0).getNameAsTopicName().getTopic());
	}

	@Test
	public void testWithPrefixNoPartition(){
		ExtendedProducerProperties<PubSubProducerProperties> producerProperties = new ExtendedProducerProperties<>(new PubSubProducerProperties());
		producerProperties.getExtension().setPrefix("bar");
		List<Topic> topics = this.resourceManager.createTopic("foo",producerProperties);
		this.topics.addAll(topics);
		Assert.assertEquals(1, topics.size());
		Assert.assertEquals("bar.foo",topics.get(0).getNameAsTopicName().getTopic());
	}
	@Test
	public void testWithPrefixWithPartition(){
		ExtendedProducerProperties<PubSubProducerProperties> producerProperties = new ExtendedProducerProperties<>(new PubSubProducerProperties());
		producerProperties.getExtension().setPrefix("bar");
		producerProperties.setPartitionCount(3);
		producerProperties.setPartitionKeyExtractorClass(String.class);
		List<Topic> topics = this.resourceManager.createTopic("foo",producerProperties);
		this.topics.addAll(topics);
		Assert.assertEquals(3, topics.size());
		Assert.assertEquals("bar.foo-0",topics.get(0).getNameAsTopicName().getTopic());
		Assert.assertEquals("bar.foo-1",topics.get(1).getNameAsTopicName().getTopic());
		Assert.assertEquals("bar.foo-2",topics.get(2).getNameAsTopicName().getTopic());
	}
	@Test
	public void testGroupedConsumersWithPartition(){
		String[] subscriptionNames = new String[] {"bar.foo-0.sensors","bar.foo-1.sensors","bar.foo-2.sensors","bar.foo-0.average","bar.foo-1.average","bar.foo-2.average"};
		ExtendedProducerProperties<PubSubProducerProperties> producerProperties = new ExtendedProducerProperties<>(new PubSubProducerProperties());
		producerProperties.getExtension().setPrefix("bar");
		producerProperties.setPartitionCount(3);
		producerProperties.setPartitionKeyExtractorClass(String.class);
		producerProperties.setRequiredGroups("sensors","average");
		List<Topic> topics = this.resourceManager.createTopic("foo",producerProperties);
		this.topics.addAll(topics);
		Assert.assertEquals(3, topics.size());
		Assert.assertEquals("bar.foo-0",topics.get(0).getNameAsTopicName().getTopic());
		Assert.assertEquals("bar.foo-1",topics.get(1).getNameAsTopicName().getTopic());
		Assert.assertEquals("bar.foo-2",topics.get(2).getNameAsTopicName().getTopic());

		List<Subscription> subscriptions = this.resourceManager.createRequiredGroups(topics,producerProperties);
		this.subscriptions.addAll(subscriptions);
		Assert.assertEquals(6,subscriptions.size());
		int matches = 0;
		for(String name : subscriptionNames){
			for(Subscription subscription : subscriptions){
				if(subscription.getNameAsSubscriptionName().getSubscription().equals(name)){
					matches++;
					break;
				}
			}
		}
		Assert.assertEquals(6,matches);

	}
}
