package com.adeptj.runtime.tomcat;

import org.apache.catalina.authenticator.FormAuthenticator;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;

public class SecurityConfigurer {

    public void configure(StandardContext context) {
        // LoginConfig
        LoginConfig loginConfig = new LoginConfig();
        loginConfig.setAuthMethod("FORM");
        loginConfig.setLoginPage("/admin/login");
        loginConfig.setErrorPage("/admin/login");
        loginConfig.setRealmName("AdeptJ Realm");
        context.setLoginConfig(loginConfig);
        // SecurityConstraint
        SecurityConstraint constraint = new SecurityConstraint();
        constraint.addAuthRole("OSGiAdmin");
        SecurityCollection collection = new SecurityCollection();
        collection.addPattern("/system/console/*");
        constraint.addCollection(collection);
        context.addConstraint(constraint);
        // Form Auth
        FormAuthenticator valve = new FormAuthenticator();
        valve.setLandingPage("/admin/login");
        context.addValve(valve);
        // Realm and CredentialHandler
        TypesafeConfigBasedRealm realm = new TypesafeConfigBasedRealm();
        realm.setCredentialHandler(new TypesafeConfigBasedCredentialHandler());
        context.setRealm(realm);
    }
}
