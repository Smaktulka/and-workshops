package by.andersen.repository;

import by.andersen.entity.Workspace;
import org.hibernate.SessionFactory;

public class WorkspaceRepository extends HibernateRepository<Workspace, Long> {

  public WorkspaceRepository(
      SessionFactory sessionFactory,
      Class<Workspace> entityClass
  ) {
    super(sessionFactory, entityClass);
  }
}
