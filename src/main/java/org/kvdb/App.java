package org.kvdb;

import org.kvdb.database.Session;
import org.kvdb.database.SessionManager;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        SessionManager sessMgr = new SessionManager();
        Session sess = sessMgr.createSession();

        sess.startTransaction();
        sess.put("foo", "bar");
        sess.put("baz", "bat");
        sess.delete("baz");
        sess.commitTransaction();
        System.out.println(sess.get("baz"));
        System.out.println(sess.get("foo"));
    }
}
