package de.szut.lf8_project.project;

import de.szut.lf8_project.project.dto.ProjectCreateDto;
import de.szut.lf8_project.project.dto.ProjectGetDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class ProjectMapper {

    public ProjectGetDto mapToGetDto(ProjectEntity entity) {
        return new ProjectGetDto(entity.getId(), entity.getName());
    }

    public ProjectEntity mapCreateDtoToEntity(ProjectCreateDto dto) {
        var entity = new ProjectEntity();
        entity.setName(dto.getName());
        entity.setClientId(dto.getClientId());
        entity.setEmployees(dto.getEmployees());
        entity.setResponsiblePersonByClientName(dto.getResponsiblePersonByClientName());
        entity.setProjectGoal(dto.getProjectGoal());
        entity.setStartDate(dto.getStartDate());
        entity.setPlannedEndDate(dto.getPlannedEndDate());
        entity.setActualEndDate(dto.getActualEndDate());
        return entity;
    }
}
