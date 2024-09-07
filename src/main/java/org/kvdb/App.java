package org.kvdb;

import org.kvdb.database.Session;
import org.kvdb.database.SessionManager;

/**
 * Example program that uses kvdb
 *
 */
public class App 
{
    public static void main( String[] args ) {
        // obtain an instance of the db
        SessionManager sessMgr = new SessionManager();
        Session sess = sessMgr.createSession();
        Session ses2 = sessMgr.createSession();

        // opertations can be performed outside a transaction and are immediately
        // applied to the backing store
        sess.put("key1", "somevalue");
        sess.startTransaction();
        sess.put("foo", "bar");
        sess.put("baz", "bat");
        sess.delete("baz");
        System.out.println(sess.get("key1"));
        sess.commitTransaction();
        System.out.println(sess.get("baz"));
        System.out.println(sess.get("foo"));

        // this transaction will not commit because of a conflicting write
        sess.startTransaction();
        sess.put("k1", "v1");
        ses2.put("k1", "v2");
        sess.commitTransaction();
        System.out.println(sess.get("k1"));
    }
}
