package org.gd.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;


import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class AiServiceClientConfig {

    @Bean
    public RestClient aiServiceRestClient(RestClient.Builder builder,AiServiceProperties props) {

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(props.readTimeout());

        return builder
                .baseUrl(props.url())
                .requestFactory(requestFactory)
                .build();
    }


}
