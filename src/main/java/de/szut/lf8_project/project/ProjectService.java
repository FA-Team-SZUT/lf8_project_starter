package de.szut.lf8_project.project;

import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository){this.repository = repository;}

    public ProjectEntity create(ProjectEntity newProject){return repository.save(newProject);}



}
