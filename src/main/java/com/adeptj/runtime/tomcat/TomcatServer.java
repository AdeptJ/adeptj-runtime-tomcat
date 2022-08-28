package com.adeptj.runtime.tomcat;

import com.adeptj.runtime.kernel.AbstractServer;
import com.adeptj.runtime.kernel.SciInfo;
import com.adeptj.runtime.kernel.ServerName;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.http.HttpServlet;
import java.io.File;

public class TomcatServer extends AbstractServer {

    private Tomcat tomcat;

    @Override
    public ServerName getName() {
        return ServerName.TOMCAT;
    }

    @Override
    public void start(String[] args, SciInfo sciInfo) {
        this.tomcat = new Tomcat();
        this.tomcat.setBaseDir("temp");
        Connector connector = this.tomcat.getConnector();
        connector.setPort(8080);

        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);
        context.addServletContainerInitializer(sciInfo.getSciInstance(), sciInfo.getHandleTypes());
        Tomcat.addServlet(context, "GreetingServlet", new GreetingServlet());
        context.addServletMappingDecoded("/greet", "GreetingServlet");

        try {
            this.tomcat.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
        this.doStart(args, "AdeptJ Tomcat Terminator");
        this.tomcat.getServer().await();

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
    public void registerServlets(HttpServlet... servlets) {

    }
}