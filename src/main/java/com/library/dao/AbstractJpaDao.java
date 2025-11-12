package com.library.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class AbstractJpaDao<T, ID> implements JpaDao<T, ID> {

    private static final String PERSISTENCE_UNIT_NAME = "libraryPU";
    private static EntityManagerFactory factory;
    protected Class<T> entityClass;

    static {
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Failed to initialize EntityManagerFactory: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public AbstractJpaDao() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    @Override
    public T findById(ID id) {
        EntityManager em = getEntityManager();
        T entity = em.find(entityClass, id);
        em.close();
        return entity;
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        TypedQuery<T> query = em.createQuery("FROM " + entityClass.getName(), entityClass);
        List<T> result = query.getResultList();
        em.close();
        return result;
    }

    @Override
    public void save(T entity) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void update(T entity) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void delete(T entity) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.remove(em.contains(entity) ? entity : em.merge(entity));
        em.getTransaction().commit();
        em.close();
    }
}
