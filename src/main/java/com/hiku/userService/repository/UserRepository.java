package com.hiku.userService.repository;

import com.hiku.userService.model.User;
import com.hiku.userService.model.Follow;

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

    public User find(String id) {
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

    public boolean delete(String id) {
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


    // ===== Follow Methods =====

    public Follow follow(User follower, User following) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Follow f = new Follow();
        f.setFollower(follower);
        f.setFollowing(following);
        em.persist(f);
        em.getTransaction().commit();
        em.close();
        return f;
    }

    public boolean unfollow(String followerId, String followingId) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Follow f = em.createQuery(
            "SELECT f FROM Follow f WHERE f.follower.id = :follower AND f.following.id = :following",
            Follow.class)
            .setParameter("follower", followerId)
            .setParameter("following", followingId)
            .getResultStream().findFirst().orElse(null);
        
        if (f == null) {
            em.getTransaction().rollback();
            em.close();
            return false;
        }
        em.remove(f);
        em.getTransaction().commit();
        em.close();
        return true;
    }

    public List<User> getFollowers(String userId) {
        EntityManager em = getEntityManager();
        List<User> result = em.createQuery(
            "SELECT f.follower FROM Follow f WHERE f.following.id = :userId",
            User.class)
            .setParameter("userId", userId)
            .getResultList();
        em.close();
        return result;
    }



public List<User> searchByUsername(String query) {
    System.out.println("Searching users with query: " + query);
    EntityManager em = getEntityManager();
    List<User> result = em.createQuery(
        "SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(:query)",
        User.class)
        .setParameter("query", "%" + query + "%")
        .getResultList();
    em.close();
    return result;
}



    public List<User> getFollowing(String userId) {
        EntityManager em = getEntityManager();
        List<User> result = em.createQuery(
            "SELECT f.following FROM Follow f WHERE f.follower.id = :userId",
            User.class)
            .setParameter("userId", userId)
            .getResultList();
        em.close();
        return result;
    }

    public long getFollowerCount(String userId) {
        EntityManager em = getEntityManager();
        long count = em.createQuery(
            "SELECT COUNT(f) FROM Follow f WHERE f.following.id = :userId",
            Long.class)
            .setParameter("userId", userId)
            .getSingleResult();
        em.close();
        return count;
    }

    public long getFollowingCount(String userId) {
        EntityManager em = getEntityManager();
        long count = em.createQuery(
            "SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId",
            Long.class)
            .setParameter("userId", userId)
            .getSingleResult();
        em.close();
        return count;
    }

    public boolean isFollowing(String followerId, String followingId) {
        EntityManager em = getEntityManager();
        Long count = em.createQuery(
            "SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :follower AND f.following.id = :following",
            Long.class)
            .setParameter("follower", followerId)
            .setParameter("following", followingId)
            .getSingleResult();
        em.close();
        return count > 0;
    }
}
