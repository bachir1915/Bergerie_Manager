package sn.isi.l3gl.core.entities;

import jakarta.persistence.*;
import lombok.*;
import sn.isi.l3gl.core.model.TaskStatus;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;
}
