package repository.impl;

import repository.CrudRepository;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

public abstract class CrudRepositoryImpl<T, ID extends Serializable> implements CrudRepository<T, ID> {

    protected EntityManager entityManager;
    private Class<T> persistentClass;

    @Override
    public Optional<T> find(T entityForSearch) {
        ID id = (ID) entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entityForSearch);
        return findById(id);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entityManager.find(persistentClass, id));
    }

    @Transactional
    @Override
    @SuppressWarnings("unchecked")
    public Set<T> findAll() {
        entityManager.clear();
        return new HashSet<T>(entityManager.createQuery("Select t from " + persistentClass.getSimpleName() + " t")
                                      .setHint("javax.persistence.cache.storeMode", "REFRESH").getResultList());
    }


    public T save(T entityForSave) {
        try {
            entityManager.getTransaction().begin();
            T savedEntity = entityManager.merge(entityForSave);
            entityManager.getTransaction().commit();
            return savedEntity;
        } catch (Exception exception) {
            entityManager.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public T create(T entityForCreate) {
        return save(entityForCreate);
    }

    @Override
    public T update(T entityForUpdate) {
        return find(entityForUpdate).map(x -> save(entityForUpdate))
                .orElseThrow(() -> new NotFoundException(persistentClass.getSimpleName() + " not found!"));
//        try {
//            entityManager.getTransaction().begin();
//            T updatedEntity = entityManager.merge(entityForUpdate);
//            entityManager.getTransaction().commit();
//            return updatedEntity;
//        } catch (Exception exception) {
//            entityManager.getTransaction().rollback();
//            return null;
//        }

    }

    @Override
    public void deleteEntity(T entityForDelete) {
        T foundEntity = find(entityForDelete).orElseThrow(
                () -> new NotFoundException(persistentClass.getSimpleName() + "not found!"));
        delete(foundEntity);
    }

    @Override
    public void deleteById(ID entityIdForDelete) {
        T entityForDelete = findById(entityIdForDelete).orElseThrow(
                () -> new NotFoundException(persistentClass.getSimpleName() +
                        " not found! entityIdForDelete: " + entityIdForDelete.toString()));
        delete(entityForDelete);
    }

    private void delete(T entityForDelete) {
        entityManager.getTransaction().begin();
        entityManager.remove(entityForDelete);
        entityManager.getTransaction().commit();
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    private void postConstruct() {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
//        this.persistentClass = (Class<T>) ((ParameterizedTypeImpl) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.persistentClass = (Class<T>) ((ParameterizedTypeImpl) ((Class) getClass().getGenericSuperclass()).getGenericSuperclass()).getActualTypeArguments()[0];
        this.entityManager = Persistence.createEntityManagerFactory("BatchBranchPublisherPersistenceUnit")
                .createEntityManager();
    }

}
