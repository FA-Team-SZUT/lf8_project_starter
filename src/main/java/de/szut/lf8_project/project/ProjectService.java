package de.szut.lf8_project.project;

import de.szut.lf8_project.exceptionHandling.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public ProjectEntity readById(long id) {
        Optional<ProjectEntity> optionalQualification = this.repository.findById(id);
        if (optionalQualification.isEmpty()) {
            throw new ResourceNotFoundException("Project not found on id = " + id);
        }
        return optionalQualification.get();
    }
}
