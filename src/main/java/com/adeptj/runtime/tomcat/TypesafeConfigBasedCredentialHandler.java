package com.adeptj.runtime.tomcat;

import org.apache.catalina.CredentialHandler;

public class TypesafeConfigBasedCredentialHandler implements CredentialHandler {

    @Override
    public boolean matches(String inputCredentials, String storedCredentials) {
        return inputCredentials.equals("admin");
    }

    @Override
    public String mutate(String inputCredentials) {
        return inputCredentials;
    }
}
