package com.adeptj.runtime.tomcat;

import org.apache.catalina.Wrapper;
import org.apache.catalina.authenticator.FormAuthenticator;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;

public class SecurityConfigurer {

    public void configure(StandardContext context) {
        // ContextPathFilter
        FilterDef def = new FilterDef();
        def.setAsyncSupported("true");
        def.setFilterName("ContextPathFilter");
        def.setDisplayName("Filter for handling / requests");
        def.setFilter(new ContextPathFilter());
        context.addFilterDef(def);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("ContextPathFilter");
        filterMap.addURLPattern("/*");
        context.addFilterMap(filterMap);
        // ResourceServlet
        Wrapper defaultServlet = context.createWrapper();
        defaultServlet.setName("default");
        defaultServlet.setServletClass(ResourceServlet.class.getName());
        defaultServlet.addInitParameter("debug", "0");
        defaultServlet.addInitParameter("listings", "false");
        defaultServlet.setLoadOnStartup(1);
        context.addChild(defaultServlet);
        context.addServletMappingDecoded("/static/*", "default");
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
        valve.setLandingPage("/");
        valve.setCharacterEncoding("UTF-8");
        context.addValve(valve);
        // Realm and CredentialHandler
        TypesafeConfigBasedRealm realm = new TypesafeConfigBasedRealm();
        realm.setCredentialHandler(new TypesafeConfigBasedCredentialHandler());
        context.setRealm(realm);
    }
}
