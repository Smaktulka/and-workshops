package by.andersen.repository;

import by.andersen.entity.Workspace;
import javax.sql.DataSource;

public class WorkspaceRepository extends JdbcRepository<Workspace, Long> {

  public WorkspaceRepository(
      DataSource dataSource,
      String currentSchema,
      String tableName,
      Class<Workspace> entityClass
  ) {
    super(dataSource, currentSchema, tableName, entityClass);
  }
}
