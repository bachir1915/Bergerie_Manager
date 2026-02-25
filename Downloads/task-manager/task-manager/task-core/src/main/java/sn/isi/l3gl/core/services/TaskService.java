package sn.isi.l3gl.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.isi.l3gl.core.entities.Task;
import sn.isi.l3gl.core.model.TaskStatus;
import sn.isi.l3gl.core.repositories.TaskRepository;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    public Task updateStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public long countCompletedTasks() {
        return taskRepository.countByStatus(TaskStatus.DONE);
    }
}
