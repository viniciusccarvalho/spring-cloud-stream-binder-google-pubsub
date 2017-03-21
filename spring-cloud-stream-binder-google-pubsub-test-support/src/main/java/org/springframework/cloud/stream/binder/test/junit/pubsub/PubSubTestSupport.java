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

package org.springframework.cloud.stream.binder.test.junit.pubsub;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.grpc.InstantiatingChannelProvider;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.deprecated.testing.LocalPubSubHelper;
import com.google.cloud.pubsub.spi.v1.PublisherClient;
import com.google.cloud.pubsub.spi.v1.PublisherSettings;
import com.google.cloud.pubsub.spi.v1.SubscriberClient;
import com.google.cloud.pubsub.spi.v1.SubscriberSettings;
import org.joda.time.Duration;
import org.mockito.Mockito;

import org.springframework.cloud.stream.test.junit.AbstractExternalResourceTestSupport;
import org.springframework.util.StringUtils;

/**
 * @author Vinicius Carvalho
 */
public class PubSubTestSupport  extends AbstractExternalResourceTestSupport<Object> {

	private Object stateHolder = null;
	private LocalPubSubHelper helper;
	private PublisherClient publisherClient;
	private SubscriberClient subscriberClient;
	private String projectId;
	private ObjectMapper mapper = new ObjectMapper();

	public PubSubTestSupport() {
		super("PUBSUB");
	}

	@Override
	protected void cleanupResource() throws Exception {
		if(helper != null){
			helper.stop(Duration.millis(1000));
		}
	}

	@Override
	protected void obtainResource() throws Exception {
		String jsonCred = System.getenv("GOOGLE_CLOUD_JSON_CRED");
		if(StringUtils.hasText(jsonCred)){
			Map<String,Object> credentialMap = mapper.readValue(jsonCred,Map.class);

			this.projectId = credentialMap.get("project_id").toString();
			ServiceAccountCredentials credentials =ServiceAccountCredentials
					.fromStream(new ByteArrayInputStream(jsonCred.getBytes()));

			InstantiatingChannelProvider channelProvider = PublisherSettings.defaultChannelProviderBuilder().setCredentialsProvider(FixedCredentialsProvider
					.create(credentials.createScoped(Collections.singleton("https://www.googleapis.com/auth/pubsub")))).build();
			PublisherSettings settings = PublisherSettings
					.defaultBuilder()
					.setChannelProvider(channelProvider).build();
			SubscriberSettings subscriberSettings = SubscriberSettings.defaultBuilder().setChannelProvider(channelProvider).build();
			this.publisherClient = PublisherClient.create(settings);
			this.subscriberClient = SubscriberClient.create(subscriberSettings);
			this.stateHolder = new Object();
		}
	}

	public PublisherClient getPublisherClient() {
		return this.publisherClient;
	}

	public SubscriberClient getSubscriberClient(){
		return this.subscriberClient;
	}

	public String getProjectId() {
		return projectId;
	}

	public static class FakeRefreshCredentials extends OAuth2Credentials {

		@Override
		public AccessToken refreshAccessToken() throws IOException {
			return Mockito.mock(AccessToken.class);
		}
	}
}
