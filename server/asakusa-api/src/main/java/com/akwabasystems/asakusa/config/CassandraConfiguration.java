
package com.akwabasystems.asakusa.config;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.annotation.PreDestroy;
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
    private String keyspace;

    @Value("${cassandra.local-data-center}")
    private String localDataCenterName;

    @Value("${aws.access-key}")
    private String username;

    @Value("${aws.secret-key}")
    private String password;
    
    public CassandraConfiguration() {}
    
    public CassandraConfiguration(String cassandraHost, 
                                  int port, 
                                  String localDataCenterName, 
                                  String keyspace) {
        super();
        this.cassandraHost = cassandraHost;
        this.cassandraPort = port;
        this.localDataCenterName = localDataCenterName;
        this.keyspace = keyspace;
    }
    
    @Bean
    public CqlIdentifier keyspaceName() {
        return CqlIdentifier.fromCql(getKeyspace());
    }
    
    @Bean
    public CqlSession cqlSession() {
        CqlSession session = null;

        try {

            session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(getCassandraHost(), getCassandraPort()))
                    .withSslContext(SSLContext.getDefault())
                    .withLocalDatacenter(getLocalDataCenterName())
                    .withAuthCredentials(getUsername(), getPassword())
                    .withKeyspace(CqlIdentifier.fromCql(getKeyspace()))
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

    public void setCassandraHost(String cassandraHost) {
        this.cassandraHost = cassandraHost;
    }

    public int getCassandraPort() {
        return cassandraPort;
    }

    public void setCassandraPort(int port) {
        this.cassandraPort = port;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public String getLocalDataCenterName() {
        return localDataCenterName;
    }

    public void setLocalDataCenterName(String localDataCenterName) {
        this.localDataCenterName = localDataCenterName;
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
