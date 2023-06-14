package com.akwabasystems.asakusa.config;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.SessionFactoryFactoryBean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = {"com.akwabasystems.asakusa"})
public class CassandraConfig {

    @Value("${cassandra.endpoint}")
    private String endpoint;

    @Value("${cassandra.port}")
    private int port;

    @Value("${cassandra.keyspace}")
    private String keyspace;

    @Value("${cassandra.dataCenter}")
    private String dataCenter;

    @Value("${aws.accessKey}")
    private String username;

    @Value("${aws.secretKey}")
    private String password;

    @Bean
    public CqlSession session() {
        CqlSession session = null;

        try {

            InetSocketAddress address = new InetSocketAddress(endpoint, port);
            List<InetSocketAddress> contactPoints = Collections.singletonList(address);

            session = CqlSession.builder()
                    .addContactPoints(contactPoints)
                    .withSslContext(SSLContext.getDefault())
                    .withLocalDatacenter(dataCenter)
                    .withAuthCredentials(username, password)
                    .withKeyspace(CqlIdentifier.fromCql(keyspace))
                    .build();

            System.out.printf("[Cassandra] Session initialized - keyspace: %s, endpoint: %s%n",
                    session.getKeyspace().get(), endpoint);

        } catch (Exception ex) {
            System.out.println("Exception occurred while creating Cassandra session: " + ex.getMessage());
        }

        return session;
    }

    @Bean
    public SessionFactoryFactoryBean sessionFactory(CqlSession session, CassandraConverter converter) {
        SessionFactoryFactoryBean sessionFactory = new SessionFactoryFactoryBean();
        sessionFactory.setSession(session);
        sessionFactory.setConverter(converter);
        sessionFactory.setSchemaAction(SchemaAction.NONE);

        return sessionFactory;
    }

    @Bean
    public CassandraMappingContext mappingContext(CqlSession cqlSession) {
        CassandraMappingContext mappingContext = new CassandraMappingContext();
        mappingContext.setUserTypeResolver(new SimpleUserTypeResolver(cqlSession));

        return mappingContext;
    }

    @Bean
    public CassandraConverter converter(CassandraMappingContext mappingContext) {
        return new MappingCassandraConverter(mappingContext);
    }

    @Bean
    public CassandraOperations cassandraTemplate(SessionFactory sessionFactory, CassandraConverter converter) {
        return new CassandraTemplate(sessionFactory, converter);
    }

}
