package sn.isi.l3gl.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// scanBasePackages élargi pour détecter TaskService, TaskRepository et Task
// qui se trouvent dans le package sn.isi.l3gl.core (module task-core)
@SpringBootApplication(scanBasePackages = {"sn.isi.l3gl"})
public class TaskApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(TaskApiApplication.class, args);
	}
}
