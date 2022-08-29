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
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;

import java.io.File;

public class TomcatServer extends AbstractServer {

    private Tomcat tomcat;

    private Context context;

    @Override
    public ServerRuntime getRuntime() {
        return ServerRuntime.TOMCAT;
    }

    @Override
    public void start(String[] args, ServletDeployment deployment) {
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
        SciInfo sciInfo = deployment.getSciInfo();
        this.context.addServletContainerInitializer(sciInfo.getSciInstance(), sciInfo.getHandleTypes());
        this.registerServlets(deployment.getServletInfos());
        new SecurityConfigurer().configure((StandardContext) this.context);
        Tomcat.addDefaultMimeTypeMappings(this.context);
        try {
            this.tomcat.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
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
}