package com.github.h2m.revision;

import org.hibernate.envers.RevisionListener;


public class UserRevisionListener implements RevisionListener {

    public final static String USERNAME = "h2m";

    @Override
    public void newRevision(Object revisionEntity) {
        UserRevEntity exampleRevEntity = (UserRevEntity) revisionEntity;
        exampleRevEntity.setUsername(USERNAME);
    }
}
