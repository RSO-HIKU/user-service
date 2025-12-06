package com.hiku.userService.dao;

import com.hiku.userService.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class UserRepository {

    @PersistenceContext(unitName = "hikuPU")
    private EntityManager em;

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User find(Long id) {
        return em.find(User.class, id);
    }

    public User create(User user) {
        em.persist(user);
        return user;
    }

    public User update(User user) {
        if (user.getId() == null || em.find(User.class, user.getId()) == null) return null;
        return em.merge(user);
    }

    public boolean delete(Long id) {
        User u = em.find(User.class, id);
        if (u == null) return false;
        em.remove(u);
        return true;
    }
}