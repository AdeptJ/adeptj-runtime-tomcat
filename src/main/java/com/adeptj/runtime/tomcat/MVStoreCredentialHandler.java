package com.adeptj.runtime.tomcat;

import com.adeptj.runtime.kernel.UserManager;
import org.apache.catalina.CredentialHandler;

public class MVStoreCredentialHandler implements CredentialHandler {

    private final UserManager userManager;

    public MVStoreCredentialHandler(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public boolean matches(String inputCredentials, String storedCredentials) {
        return this.userManager.matchPassword(inputCredentials, storedCredentials);
    }

    @Override
    public String mutate(String inputCredentials) {
        return inputCredentials;
    }
}
