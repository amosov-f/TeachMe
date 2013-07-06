package com.kk.vkrent;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.session.JDBCSessionIdManager;
import org.eclipse.jetty.server.session.JDBCSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Server jettyServer = null;
        try {
            System.out.println(new File(".").getAbsolutePath());
            jettyServer = new Server();

            SocketConnector conn = new SocketConnector();
            conn.setPort(8080);
            jettyServer.setConnectors(new Connector[]{conn});

            WebAppContext context = new WebAppContext();

            //           setJDBCmanager(jettyServer, context);
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

    private static void setJDBCmanager(Server server, WebAppContext ctx) {
        JDBCSessionIdManager idMgr = new JDBCSessionIdManager(server);
        idMgr.setDriverInfo("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/vkrent?user=root");
        idMgr.setWorkerName("fred");
        idMgr.setScavengeInterval(60);
        server.setSessionIdManager(idMgr);

        JDBCSessionManager jdbcMgr = new JDBCSessionManager();
        jdbcMgr.setSessionIdManager(server.getSessionIdManager());
        ctx.setSessionHandler(new SessionHandler(jdbcMgr));
    }
}

