package com.increff.pos.dao;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.InventoryPojo;

@Repository
public class InventoryDao extends AbstractDao<InventoryPojo> {

    InventoryDao() {
        super(InventoryPojo.class);
    }

}
