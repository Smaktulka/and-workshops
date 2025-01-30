package by.andersen.repository;

import by.andersen.entity.Workspace;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WorkspaceRepository extends HibernateRepository<Workspace, Long> {
  @Autowired
  public WorkspaceRepository(SessionFactory sessionFactory) {
    super(sessionFactory, Workspace.class);
  }
}
