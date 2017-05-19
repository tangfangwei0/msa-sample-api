package demo.msa.sample;

import demo.msa.framework.registry.ServiceRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by tangfw on 2017/5/14.
 */
@Configuration
@ConfigurationProperties(prefix = "registry")
public class RegistryConfig {
    private String servers;

    @Bean
    public ServiceRegistry ServiceRegistry(){
        return new ServiceRegistryImpl(servers);
    }

    public void setServers(String servers){
        this.servers = servers;
    }


}
