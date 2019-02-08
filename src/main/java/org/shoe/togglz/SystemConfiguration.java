package org.shoe.togglz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.spi.FeatureProvider;

@Configuration
public class SystemConfiguration {
    @Bean
    public RestTemplate getTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FeatureProvider featureProvider() {
        return new EnumBasedFeatureProvider(FeatureToggles.class);
    }
}
