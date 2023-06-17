
package com.akwabasystems.asakusa.config;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import java.net.InetSocketAddress;
import javax.net.ssl.SSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;


@Configuration
@EnableCassandraRepositories(basePackages = {"com.akwabasystems.asakusa"})
public class CassandraConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CassandraConfiguration.class);
    
    @Value("${cassandra.contact-point}")
    private String cassandraHost;

    @Value("${cassandra.port}")
    private int cassandraPort;

    @Value("${cassandra.keyspace-name}")
    private String keyspaceName;

    @Value("${cassandra.local-data-center}")
    private String localDataCenterName;

    @Value("${aws.accessKey}")
    private String username;

    @Value("${aws.secretKey}")
    private String password;
    
    public CassandraConfiguration() {}
    
    public CassandraConfiguration(String endpoint, 
                                  int port, 
                                  String dataCenter, 
                                  String keyspaceName) {
        super();
        this.cassandraHost = endpoint;
        this.cassandraPort = port;
        this.localDataCenterName = dataCenter;
        this.keyspaceName = keyspaceName;
    }
    
    @Bean
    public CqlIdentifier keyspace() {
        return CqlIdentifier.fromCql(getKeyspaceName());
    }
    
    @Bean
    public CqlSession session() {
        CqlSession session = null;

        try {

            session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(getCassandraHost(), getCassandraPort()))
                    .withSslContext(SSLContext.getDefault())
                    .withLocalDatacenter(getLocalDataCenterName())
                    .withAuthCredentials(getUsername(), getPassword())
                    .withKeyspace(CqlIdentifier.fromCql(getKeyspaceName()))
                    .build();

            logger.info(String.format("[Cassandra] Session initialized - keyspace: %s, contact-point: %s%n",
                    session.getKeyspace().get(), getCassandraHost()));

        } catch (Exception ex) {
            logger.info("Exception occurred while creating Cassandra session: " + ex.getMessage());
        }

        return session;
    }
    
    public String getCassandraHost() {
        return cassandraHost;
    }

    public void setCassandraHost(String endpoint) {
        this.cassandraHost = endpoint;
    }

    public int getCassandraPort() {
        return cassandraPort;
    }

    public void setCassandraPort(int port) {
        this.cassandraPort = port;
    }

    public String getKeyspaceName() {
        return keyspaceName;
    }

    public void setKeyspaceName(String keyspaceName) {
        this.keyspaceName = keyspaceName;
    }

    public String getLocalDataCenterName() {
        return localDataCenterName;
    }

    public void setLocalDataCenterName(String dataCenter) {
        this.localDataCenterName = dataCenter;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
