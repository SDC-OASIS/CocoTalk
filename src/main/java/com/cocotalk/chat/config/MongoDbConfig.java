package com.cocotalk.chat.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Slf4j
@Getter
@Configuration
@EnableMongoAuditing
public class MongoDbConfig extends AbstractMongoClientConfiguration {
    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.username}")
    private String username;

    @Value("${spring.data.mongodb.password}")
    private String password;

    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    protected String getDatabaseName() {
        return getDatabase();
    }


    @Bean
    @Override
    public MongoClient mongoClient() {
        log.info("Creating Mongodb Client Configuration");

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(getConnectionString())).build();

        return MongoClients.create(mongoClientSettings);
    }

    private String getConnectionString() {
        log.info("active profile for url : {}", profile);

        String userInfo = "";
        // String conParam = "";

        if (profile.equals("dev") || profile.equals("prod")) {

            userInfo = getUsername()
                    .concat(":").concat(getPassword()).concat("@");

            // conParam = "?replicaSet=rs0&readPreference=secondaryPreferred&retryWrites=false";

        }
        String mongoUrl = String.format(
                "mongodb://%s%s:%s/%s",
                userInfo,
                getHost(),
                getPort(),
                getDatabase()
        );

        log.info("mongo url : {}", mongoUrl);

        return mongoUrl;
    }
}
