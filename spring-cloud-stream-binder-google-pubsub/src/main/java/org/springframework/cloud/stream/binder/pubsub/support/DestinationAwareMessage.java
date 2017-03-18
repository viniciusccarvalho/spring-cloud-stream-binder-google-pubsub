/*
 *  Copyright 2016 original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.springframework.cloud.stream.binder.pubsub.support;


import com.google.pubsub.v1.PubsubMessage;

/**
 * Wrapper of {@link PubsubMessage} that contains the topic that the message
 * should be published to. Useful for groupBy operation on the processor
 * @author Vinicius Carvalho
 */
public class DestinationAwareMessage {

	private final PubsubMessage message;
	private final String topic;

	public DestinationAwareMessage(PubsubMessage message, String topic) {
		this.message = message;
		this.topic = topic;
	}

	public PubsubMessage getMessage() {
		return message;
	}

	public String getTopic() {
		return topic;
	}
}
