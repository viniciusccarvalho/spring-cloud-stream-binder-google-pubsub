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

package org.springframework.cloud.stream.binder.pubsub.support;

import java.util.List;

import com.google.pubsub.v1.PubsubMessage;


/**
 * @author Vinicius Carvalho
 */
public class GroupedMessage {
	private final String topic;
	private final List<PubsubMessage> messages;

	public GroupedMessage(String topic, List<PubsubMessage> messages) {
		this.topic = topic;
		this.messages = messages;
	}

	public String getTopic() {
		return topic;
	}

	public List<PubsubMessage> getMessages() {
		return messages;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("GroupedMessage{");
		sb.append("topic='").append(topic).append('\'');
		sb.append(", messages=").append(messages.size());
		sb.append('}');
		return sb.toString();
	}
}
