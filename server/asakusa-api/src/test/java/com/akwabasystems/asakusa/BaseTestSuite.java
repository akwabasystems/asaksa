
package com.akwabasystems.asakusa;

import com.akwabasystems.asakusa.repository.AsakusaRepository;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class BaseTestSuite {

    @Value("${cassandra.contact-point}")
    protected String cassandraHost;

    @Value("${cassandra.port}")
    protected int cassandraPort;

    @Value("${cassandra.keyspace-name}")
    protected String keyspaceName;

    @Value("${cassandra.local-data-center}")
    protected String localDataCenterName;
    
    @Value("${aws.access-key}")
    protected String username;

    @Value("${aws.secret-key}")
    protected String password;
    
    @Autowired
    protected AsakusaRepository repository;
    
}
