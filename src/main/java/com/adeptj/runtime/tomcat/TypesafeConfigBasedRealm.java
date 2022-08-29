package com.adeptj.runtime.tomcat;

import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;

import java.security.Principal;
import java.util.List;

public class TypesafeConfigBasedRealm extends RealmBase {

    @Override
    protected String getPassword(String username) {
        return "admin";
    }

    @Override
    protected Principal getPrincipal(String username) {
        return new GenericPrincipal(username, "admin", List.of("OSGiAdmin"));
    }
}
