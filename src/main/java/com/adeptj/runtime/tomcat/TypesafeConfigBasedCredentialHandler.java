package com.adeptj.runtime.tomcat;

import org.apache.catalina.CredentialHandler;

public class TypesafeConfigBasedCredentialHandler implements CredentialHandler {

    @Override
    public boolean matches(String inputCredentials, String storedCredentials) {
        return true;
    }

    @Override
    public String mutate(String inputCredentials) {
        return inputCredentials;
    }
}
