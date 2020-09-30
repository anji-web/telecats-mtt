package com.example.telecatsmtt.service;

import com.example.telecatsmtt.entity.UserEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class KeycloakService {

    @Value("${keycloak.auth-server-url}")
    private String url;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.realm}")
    private String REALM;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final static Logger logger = LoggerFactory.getLogger(KeycloakService.class);

    public int createUserToken(UserEntity userEntity){

        int statusId = 0;

        try {
            UsersResource usersResource = usersResource();
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setUsername(userEntity.getUsername());
            userRepresentation.setEmail(userEntity.getEmail());
            userRepresentation.setFirstName(userEntity.getFirstName());
            userRepresentation.setLastName(userEntity.getLastName());
            userRepresentation.setEnabled(true);

            Response res = usersResource.create(userRepresentation);
            statusId = res.getStatus();

            if (statusId == 201){
            String UID = res.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            logger.info("UID : " + UID);

//            define password
                CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                credentialRepresentation.setTemporary(false);
                credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
                credentialRepresentation.setValue(userEntity.getPassword());

//            set password
                usersResource.get(UID).resetPassword(credentialRepresentation);

//            set role
                RealmResource realmResource = realmResourceResource();
                RoleRepresentation role = realmResource.roles().get("user").toRepresentation();
                realmResource.users().get(UID).roles().realmLevel().add(Arrays.asList(role));
                logger.info("Username " + userEntity.getUsername() + " created in keycloak successfully");

            }else if (statusId == 409){
                logger.info("Username " + userEntity.getUsername() + " already present in  keycloak");
            }else {
                logger.info("Username " + userEntity.getUsername() + " cannot register user in keycloak");
            }
        }catch (RuntimeException e){
            logger.error("User register error : " + e.getMessage());
            e.printStackTrace();
        }
        return statusId;
    }

    public String getToken(UserEntity userEntity){
        String response = null;
        try {
            String email = userEntity.getEmail();
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
            nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("username", userEntity.getUsername()));
            nameValuePairs.add(new BasicNameValuePair("password", userEntity.getPassword()));
            nameValuePairs.add(new BasicNameValuePair("client_secret", clientSecret));

            response = sendPost(nameValuePairs);

        }catch (Exception e) {
            e.printStackTrace();
            logger.error("generate token error : " + e.getMessage());
        }
        return response;
    }

    public UsersResource usersResource(){
        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(url).realm("master").username("secretcode").password("2827180315")
                .clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

        RealmResource realmResource = keycloak.realm(REALM);
        UsersResource usersResource = realmResource.users();
        return usersResource;
    }

    public RealmResource realmResourceResource(){
        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(url).realm("master").username("secretcode").password("2827180315")
                .clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

        RealmResource realmResource = keycloak.realm(REALM);
        return realmResource;
    }

    private String sendPost(List<NameValuePair> urlParam) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url + "/realms/" + REALM + "/protocol/openid-connect/token" );
        post.setEntity(new UrlEncodedFormEntity(urlParam));

        HttpResponse response = client.execute(post);

        InputStream in;
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer buffer = new StringBuffer();
        String line = "";

        while ((line = reader.readLine()) != null){
            buffer.append(line);
        }

        return buffer.toString();

    }

}
