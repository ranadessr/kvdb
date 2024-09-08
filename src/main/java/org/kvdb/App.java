package org.kvdb;

import org.kvdb.database.Session;
import org.kvdb.database.SessionManager;
import org.kvdb.server.KvDbServer;

/**
 * Example program that uses kvdb
 *
 */
public class App 
{
    public static void main( String[] args ) {
        // obtain an instance of the db
        SessionManager sessMgr = new SessionManager();
        int port = 8080;
        if (System.getProperty("port") != null) {
            port = Integer.parseInt(System.getProperty("port"));
        }

        KvDbServer server = new KvDbServer(sessMgr, port);
        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
