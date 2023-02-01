package de.szut.lf8_project.project;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "project")
public class ProjectController {
    private final ProjectService service;
    private final ProjectMapper projectMapper;

    public ProjectController(ProjectService service, ProjectMapper mappingService) {
        this.service = service;
        this.projectMapper = mappingService;
    }
}
