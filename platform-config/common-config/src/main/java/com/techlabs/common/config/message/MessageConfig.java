package com.techlabs.common.config.message;

import com.techlabs.platform.core.message.MessageProvider;
import com.techlabs.platform.core.message.MessageProviderImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {
	@Bean
	public MessageProvider messageProvider(MessageSource ms) {
		return new MessageProviderImpl(ms);
	}
}
