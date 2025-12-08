package com.hiku.userService.dao;

import com.hiku.userService.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

@ApplicationScoped
public class UserRepository {

    private final EntityManagerFactory emf;

    public UserRepository() {
        // Create EntityManagerFactory for RESOURCE_LOCAL unit
        this.emf = Persistence.createEntityManagerFactory("hikuPU");
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<User> findAll() {
        EntityManager em = getEntityManager();
        List<User> result = em.createQuery("SELECT u FROM User u", User.class).getResultList();
        em.close();
        return result;
    }

    public User find(Long id) {
        EntityManager em = getEntityManager();
        User user = em.find(User.class, id);
        em.close();
        return user;
    }

    public User create(User user) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        em.close();
        return user;
    }

    public User update(User user) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        User existing = em.find(User.class, user.getId());
        if (existing == null) {
            em.getTransaction().rollback();
            em.close();
            return null;
        }
        User merged = em.merge(user);
        em.getTransaction().commit();
        em.close();
        return merged;
    }

    public boolean delete(Long id) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        User u = em.find(User.class, id);
        if (u == null) {
            em.getTransaction().rollback();
            em.close();
            return false;
        }
        em.remove(u);
        em.getTransaction().commit();
        em.close();
        return true;
    }
}
