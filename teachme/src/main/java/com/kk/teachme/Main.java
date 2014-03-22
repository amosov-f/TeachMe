package com.kk.teachme;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Server jettyServer = null;
        try {
            System.out.println(new File(".").getAbsolutePath());
            jettyServer = new Server();

            SocketConnector conn = new SocketConnector();
            conn.setPort(8083);
            jettyServer.setConnectors(new Connector[]{conn});

            WebAppContext context = new WebAppContext();

            context.setContextPath("/");
            context.setWar("teachme/src/main/webapp");

            jettyServer.setHandler(context);
            jettyServer.start();
            System.out.println("Server started!");
        } catch (Throwable ignore) {
            if (jettyServer != null) {
                try {
                    jettyServer.stop();
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            }
            ignore.printStackTrace();
        }
    }
}

