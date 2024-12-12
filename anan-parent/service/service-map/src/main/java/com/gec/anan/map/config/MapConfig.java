package com.gec.anan.map.config;

import com.gec.anan.map.repository.OrderServiceLocationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MapConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
   /* @Bean
    public OrderServiceLocationRepository orderServiceLocationRepository(){return new OrderServiceLocationRepository(); }
*/

}
