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

import com.google.cloud.pubsub.spi.v1.PublisherClient;
import com.google.cloud.pubsub.spi.v1.SubscriberClient;

/**
 * Latest PubSub client was split in {@link com.google.cloud.pubsub.spi.v1.PublisherClient} and {@link com.google.cloud.pubsub.spi.v1.SubscriberClient}.
 * This class wraps both entities reducing the number of dependencies on services that depends on pubsub;
 *
 * @author Vinicius Carvalho
 */
public class PubsubConnection {

	private final PublisherClient publisherClient;
	private final SubscriberClient subscriberClient;

	public PubsubConnection(PublisherClient publisherClient, SubscriberClient subscriberClient) {
		this.publisherClient = publisherClient;
		this.subscriberClient = subscriberClient;
	}

	public PublisherClient getPublisherClient() {
		return publisherClient;
	}

	public SubscriberClient getSubscriberClient() {
		return subscriberClient;
	}
}
