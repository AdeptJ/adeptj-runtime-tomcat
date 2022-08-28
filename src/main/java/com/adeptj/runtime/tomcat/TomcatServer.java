package com.adeptj.runtime.tomcat;

import com.adeptj.runtime.kernel.AbstractServer;
import com.adeptj.runtime.kernel.SciInfo;
import com.adeptj.runtime.kernel.ServerName;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;

import javax.servlet.http.HttpServlet;
import java.io.File;

public class TomcatServer extends AbstractServer {

    private Tomcat tomcat;

    private Context context;

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
        ProtocolHandler ph = connector.getProtocolHandler();
        if (ph instanceof Http11NioProtocol) {
            ((Http11NioProtocol) ph).setRelaxedPathChars("[]|");
        }
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();
        this.context = tomcat.addContext(contextPath, docBase);
        this.context.addServletContainerInitializer(sciInfo.getSciInstance(), sciInfo.getHandleTypes());
        Tomcat.addServlet(this.context, "GreetingServlet", new GreetingServlet());
        this.context.addServletMappingDecoded("/greet", "GreetingServlet");
        try {
            this.tomcat.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
        this.doStart(args, "AdeptJ Tomcat Terminator");
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
        this.tomcat.getServer().await();
    }

    @Override
    public void registerServlets(HttpServlet... servlets) {
        int count = 0;
        for (HttpServlet servlet : servlets) {
            String servletName = servlet.getClass().getSimpleName();
            Tomcat.addServlet(context, servletName, servlet);
            this.context.addServletMappingDecoded("/servlet" + count, servletName);
            count++;
        }
    }
}