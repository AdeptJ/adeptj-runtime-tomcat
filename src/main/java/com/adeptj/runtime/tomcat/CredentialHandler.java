package com.adeptj.runtime.tomcat;

public class CredentialHandler implements org.apache.catalina.CredentialHandler {

    @Override
    public boolean matches(String inputCredentials, String storedCredentials) {
        return true;
    }

    @Override
    public String mutate(String inputCredentials) {
        return inputCredentials;
    }
}
