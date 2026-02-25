package sn.isi.l3gl.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.isi.l3gl.core.entities.Task;
import sn.isi.l3gl.core.model.TaskStatus;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    long countByStatus(TaskStatus status);
}
