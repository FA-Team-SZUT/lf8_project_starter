package de.szut.lf8_project.project;

import de.szut.lf8_project.employee.EmployeeEntity;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "project")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<EmployeeEntity> employees;

    private Long clientId;
    private String responsiblePersonByClientName;
    private String projectGoal;
    private LocalDate startDate;
    private LocalDate plannedEndDate;
    private LocalDate actualEndDate;

    public ProjectEntity(String name) {
        this.name = name;
    }
}