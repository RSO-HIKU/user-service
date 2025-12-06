package com.hiku.userService.util;

import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;

public class SchemaGenerator {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hikuPU");
        emf.close();
        System.out.println("Schema generation finished (check target/schema-user.sql).");
    }
}