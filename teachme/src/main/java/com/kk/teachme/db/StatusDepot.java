package com.kk.teachme.db;

import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Status;
import com.kk.teachme.model.User;

/**
 * Created with IntelliJ IDEA.
 * User: Mary
 * Date: 12.07.13
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public class StatusDepot {
    Status getStatus(User user, Problem problem) {
        return Status.NEW;
    }

    boolean setStatus(User user, Problem problem, Status status) {
        return false;
    }
}
