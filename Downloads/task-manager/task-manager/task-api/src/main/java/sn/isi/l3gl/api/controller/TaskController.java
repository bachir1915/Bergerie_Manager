package sn.isi.l3gl.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sn.isi.l3gl.core.entities.Task;
import sn.isi.l3gl.core.model.TaskStatus;
import sn.isi.l3gl.core.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService; // Vient de task-core

    @PostMapping
    public Task create(@RequestBody Task t) {
        return taskService.createTask(t);
    }

    @GetMapping
    public List<Task> list() {
        return taskService.listTasks();
    }

    @PutMapping("/{id}/status")
    public Task update(@PathVariable Long id, @RequestParam String status) {
        return taskService.updateStatus(id, TaskStatus.valueOf(status.toUpperCase()));
    }

    @GetMapping("/done/count")
    public long count() {
        return taskService.countCompletedTasks();
    }
}
