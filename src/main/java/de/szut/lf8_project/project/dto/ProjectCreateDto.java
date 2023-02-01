package de.szut.lf8_project.project.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.szut.lf8_project.employee.EmployeeEntity;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class ProjectCreateDto {

    @Size(min = 3, message = "at least length of 3")
    private String name;
    private Long clientId;
    private Set<EmployeeEntity> employees;
    private String responsiblePersonByClientName;
    private String projectGoal;
    private LocalDate startDate;
    private LocalDate plannedEndDate;
    private LocalDate actualEndDate;

    @JsonCreator
    public ProjectCreateDto(String name) {
        this.name = name;
    }
}