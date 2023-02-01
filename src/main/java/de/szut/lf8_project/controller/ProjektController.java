package de.szut.lf8_project.controller;

import de.szut.lf8_project.model.Projekt;
import de.szut.lf8_project.repository.ProjektRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.ArrayList;

@Controller
public class ProjektController {
    @Autowired
    private ProjektRepository projektRepo;

    @GetMapping("/projekt")
    public String listAll(Model model)
    {
        List<Projekt>listProjekt = projektRepo.findAll();
        model.addAttribute("listProjekt",listProjekt);

        return "projekt";
    }
}
