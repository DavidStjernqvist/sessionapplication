package com.company.sessionapplication.Utils;
import com.company.sessionapplication.Models.User;

import java.util.HashMap;
import java.util.HashSet;

public class SessionKeeper {
    private static SessionKeeper sessionKeeper = null;

    private HashSet<String> validSessions = new HashSet<String>();

    public boolean checkSession(String session){
        return validSessions.contains(session);
    }
    public void addSession(String session){
        validSessions.add(session);
    }
    public void RemoveSession(String session){
        if (validSessions.contains(session))
            validSessions.remove(session);
    }
    public static SessionKeeper getSessionInstance(){
        if(sessionKeeper == null)
            sessionKeeper = new SessionKeeper();
        return sessionKeeper;
    }
}
