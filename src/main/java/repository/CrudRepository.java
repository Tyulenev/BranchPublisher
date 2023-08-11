package repository;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

public interface CrudRepository<T, ID extends Serializable> {

    Optional<T> findById(ID id);

    Optional<T> find(T entityForSearch);

    Set<T> findAll();

//    T save(T entityForSave);

    T create(T entityForCreate);

    T update(T entityForUpdate);

    void deleteEntity(T entityForDelete);

    void deleteById(ID entityIdForDelete);

}
