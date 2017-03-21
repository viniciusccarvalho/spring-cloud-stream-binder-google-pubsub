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

package org.springframework.cloud.stream.binder.pubsub.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binder.ExtendedBindingProperties;

/**
 * @author Vinicius Carvalho
 */
@ConfigurationProperties("spring.cloud.stream.pubsub")
public class PubSubExtendedBindingProperties implements
		ExtendedBindingProperties<PubSubConsumerProperties, PubSubProducerProperties> {

	private Map<String, PubSubBindingProperties> bindings = new HashMap<>();

	private PubSubBinderProperties binder = new PubSubBinderProperties();

	public Map<String, PubSubBindingProperties> getBindings() {
		return bindings;
	}

	public void setBindings(Map<String, PubSubBindingProperties> bindings) {
		this.bindings = bindings;
	}

	public PubSubBinderProperties getBinder() {
		return binder;
	}

	public void setBinder(PubSubBinderProperties binder) {
		this.binder = binder;
	}

	@Override
	public PubSubConsumerProperties getExtendedConsumerProperties(String channelName) {
		if (bindings.containsKey(channelName)
				&& bindings.get(channelName).getConsumer() != null) {
			return bindings.get(channelName).getConsumer();
		}
		else {
			return new PubSubConsumerProperties();
		}
	}

	@Override
	public PubSubProducerProperties getExtendedProducerProperties(String channelName) {
		if (bindings.containsKey(channelName)
				&& bindings.get(channelName).getProducer() != null) {
			return bindings.get(channelName).getProducer();
		}
		else {
			return new PubSubProducerProperties();
		}
	}
}
