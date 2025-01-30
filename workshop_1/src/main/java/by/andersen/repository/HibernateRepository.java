package by.andersen.repository;

import jakarta.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class HibernateRepository<T extends Serializable, ID> implements Repository<T, ID>, Serializable {
  private final SessionFactory sessionFactory;
  private final Class<T> entityClass;

  public HibernateRepository(SessionFactory sessionFactory, Class<T> entityClass) {
    this.sessionFactory = sessionFactory;
    this.entityClass = entityClass;
  }

  @Override
  public void save(T entity) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.persist(entity);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }

      throw new RuntimeException("Cannot save entity", e);
    }
  }

  @Override
  public Optional<T> findById(ID id) {
    try (Session session = sessionFactory.openSession()) {
      return Optional.ofNullable(session.get(entityClass, id));
    } catch (Exception e) {
      throw new RuntimeException("Cannot find by id", e);
    }
  }

  @Override
  public List<T> findAll() {
    try (Session session = sessionFactory.openSession()) {
      CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(entityClass);
      criteriaQuery.from(entityClass);
      return session.createQuery(criteriaQuery).getResultList();
    } catch (Exception e) {
      throw new RuntimeException("Cannot findAll", e);
    }
  }

  @Override
  public void delete(ID id) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      T entity = session.get(entityClass, id);

      if (entity != null) {
        session.remove(entity);
      }

      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }

      throw new RuntimeException("Cannot delete entity", e);
    }
  }
}
