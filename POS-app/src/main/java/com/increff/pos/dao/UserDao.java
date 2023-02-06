package com.increff.pos.dao;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.UserPojo;

@Repository
public class UserDao extends AbstractDao<UserPojo> {

    UserDao() {
        super(UserPojo.class);
    }
}


