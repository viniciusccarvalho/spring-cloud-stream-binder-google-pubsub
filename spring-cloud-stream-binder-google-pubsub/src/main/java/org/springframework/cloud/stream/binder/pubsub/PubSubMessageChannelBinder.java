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
package org.springframework.cloud.stream.binder.pubsub;


import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.cloud.stream.binder.pubsub.properties.PubSubConsumerProperties;
import org.springframework.cloud.stream.binder.pubsub.properties.PubSubProducerProperties;
import org.springframework.cloud.stream.binder.pubsub.provisioning.GooglePubsubProvisioner;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;


/**
 * @author Vinicius Carvalho
 */
public class PubSubMessageChannelBinder
		extends AbstractMessageChannelBinder<ExtendedConsumerProperties<PubSubConsumerProperties>,
		ExtendedProducerProperties<PubSubProducerProperties>, GooglePubsubProvisioner>
		implements
		ExtendedPropertiesBinder<MessageChannel, PubSubConsumerProperties, PubSubProducerProperties> {


	public PubSubMessageChannelBinder(GooglePubsubProvisioner provisioner){
		super(true,new String[0],provisioner);
	}


	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination, ExtendedProducerProperties<PubSubProducerProperties> producerProperties) throws Exception {
		return null;
	}

	@Override
	protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group, ExtendedConsumerProperties<PubSubConsumerProperties> properties) throws Exception {
		return null;
	}

	@Override
	public PubSubConsumerProperties getExtendedConsumerProperties(String channelName) {
		return null;
	}

	@Override
	public PubSubProducerProperties getExtendedProducerProperties(String channelName) {
		return null;
	}
}
