package org.springframework.cloud.stream.binder.pubsub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.cloud.AuthCredentials;
import com.google.cloud.pubsub.PubSub;
import com.google.cloud.pubsub.PubSubOptions;
import com.google.cloud.pubsub.ReceivedMessage;
import com.google.cloud.pubsub.Subscription;
import com.google.cloud.pubsub.SubscriptionInfo;
import com.google.cloud.pubsub.TopicInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.pubsub.properties.PubSubProducerProperties;
import org.springframework.cloud.stream.binder.test.junit.pubsub.PubSubTestSupport;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Vinicius Carvalho
 */
public class PubSubMessageHandlerTests {

	@Rule
	public PubSubTestSupport rule = new PubSubTestSupport();

	private PubSubResourceManager resourceManager;
	private Logger logger = LoggerFactory.getLogger(PubSubMessageHandlerTests.class);

	private PubSub pubSub;

	@Before
	public void setup() throws Exception{
		this.resourceManager = new PubSubResourceManager(rule.getResource());
		this.pubSub = rule.getResource();
	}

	@Test
	public void consumeMessages() throws Exception {

		int messageCount = 2000;
		final AtomicInteger counter = new AtomicInteger(0);
		CountDownLatch latch = new CountDownLatch(messageCount);
		String baseTopicName = "pubsub-test";
		ExtendedProducerProperties<PubSubProducerProperties> extendedProducerProperties = new ExtendedProducerProperties<>(new PubSubProducerProperties());
		List<TopicInfo> topics = new ArrayList<>();
		topics.add(resourceManager.declareTopic(baseTopicName,"",null));
		SubscriptionInfo subscriptionInfo = resourceManager.declareSubscription(topics.get(0).name(),"test-subscription","");
		PubSubMessageHandler messageHandler = new BatchingPubSubMessageHandler(resourceManager,extendedProducerProperties,topics);
		messageHandler.start();
		resourceManager.createConsumer(subscriptionInfo, message -> {
			counter.incrementAndGet();
			latch.countDown();
		});
		for(int j=0;j<messageCount;j++){
			String payload = "foo-"+j;
			messageHandler.handleMessage(MessageBuilder.withPayload(payload.getBytes()).build());
		}
		latch.await();
		Assert.assertEquals(messageCount,counter.get());
	}


}
