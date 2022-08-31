package com.adeptj.runtime.tomcat;

import com.adeptj.runtime.kernel.AbstractServer;
import com.adeptj.runtime.kernel.SciInfo;
import com.adeptj.runtime.kernel.ServerRuntime;
import com.adeptj.runtime.kernel.ServletDeployment;
import com.adeptj.runtime.kernel.ServletInfo;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.JarResourceSet;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;

import java.io.File;
import java.util.stream.Stream;

public class TomcatServer extends AbstractServer {

    private Tomcat tomcat;

    private StandardContext context;

    @Override
    public ServerRuntime getRuntime() {
        return ServerRuntime.TOMCAT;
    }

    @Override
    public void start(String[] args, ServletDeployment deployment) {
        this.tomcat = new Tomcat();
        this.tomcat.setBaseDir("tomcat-deployment");
        Connector connector = this.tomcat.getConnector();
        connector.setPort(8080);
        ProtocolHandler ph = connector.getProtocolHandler();
        if (ph instanceof Http11NioProtocol) {
            ((Http11NioProtocol) ph).setRelaxedPathChars("[]|");
        }
        this.context = (StandardContext) tomcat.addContext("", new File(".").getAbsolutePath());
        SciInfo sciInfo = deployment.getSciInfo();
        this.context.addServletContainerInitializer(sciInfo.getSciInstance(), sciInfo.getHandleTypes());
        this.registerServlets(deployment.getServletInfos());
        new SecurityConfigurer().configure(this.context);
        new GeneralConfigurer().configure(this.context);
        Tomcat.addDefaultMimeTypeMappings(this.context);
        try {
            this.tomcat.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
        // Needed by Tomcat's DefaultServlet for serving static content from adeptj-runtime jar.
        this.addJarResourceSet(this.context);
    }

    @Override
    public void stop() {
        try {
            this.tomcat.stop();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postStart() {
        super.postStart();
        this.tomcat.getServer().await();
    }

    @Override
    protected void doRegisterServlet(ServletInfo info) {
        Tomcat.addServlet(this.context, info.getServletName(), info.getServletInstance());
        this.context.addServletMappingDecoded(info.getPath(), info.getServletName());
    }

    private void addJarResourceSet(Context context) {
        String docBase = context.getDocBase();
        File[] jars = new File(docBase.substring(0, docBase.length() - 1) + "/lib").listFiles();
        if (jars == null) {
            return;
        }
        Stream.of(jars)
                .filter(jar -> jar.getName().startsWith("adeptj-runtime") && jar.getName().split("-").length == 3)
                .findAny()
                .ifPresent(jar -> {
                    JarResourceSet resourceSet = new JarResourceSet();
                    resourceSet.setBase(jar.getAbsolutePath());
                    resourceSet.setInternalPath("/WEB-INF");
                    resourceSet.setWebAppMount("/");
                    context.getResources().addJarResources(resourceSet);
                });
    }
}