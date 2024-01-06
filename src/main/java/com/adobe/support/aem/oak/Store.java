package com.adobe.support.aem.oak;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

public interface Store {
    
    public void create();

    public String getType();

    default public Long createNode(String name, Repository repo) throws LoginException, RepositoryException {
        Session session = repo.login(
                new SimpleCredentials("admin", "admin".toCharArray()));
        Node root = session.getRootNode();
        Long count = 0L;
        if (root.hasNode(name)) {
            Node hello = root.getNode(name);
            count = hello.getProperty("count").getLong();
            hello.setProperty("count", count + 1);
        } else {
            root.addNode(name).setProperty("count", 1);
        }
        session.save();
        session.logout();

        return count;
    }

}
