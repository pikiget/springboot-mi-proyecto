package com.sagadis.controller;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sagadis.model.Horario;
import com.sagadis.repository.IHorarioRespository;

@Controller
@RequestMapping("/horarios")
public class HorarioController {
    @Autowired
    private IHorarioRespository repo;

    @GetMapping("/cargarHorario")
    public String cargarHorario(Model model) {
        model.addAttribute("horarios", new Horario());
        model.addAttribute("listaHorarios", repo.findAll());
        return "crudHorario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Horario horario, Model model, RedirectAttributes redirectAttrs) {
    	try {
            horario.setFecha_creacion(Date.valueOf(LocalDate.now()));
            repo.save(horario);
            redirectAttrs.addFlashAttribute("mensaje", "✅ Horario registrado correctamente");
    	}catch (Exception e) {
    		redirectAttrs.addFlashAttribute("mensaje", "❌ Error al actualizar");
		}

        return "redirect:/horarios/cargarHorario";
    }
    
	@GetMapping("/cargar/{id}")
	@ResponseBody
	public Horario cargarUsuarioPorId(@PathVariable("id") int id) {
	    return repo.findById(id).orElse(null);
	}

	@PostMapping("/actualizar")
	public String actualizar(@ModelAttribute Horario horario, RedirectAttributes redirectAttrs) {
		try {
		    horario.setFecha_modificacion(Date.valueOf(LocalDate.now()));
		    repo.save(horario);
		    redirectAttrs.addFlashAttribute("mensaje", "✅ Horario actualizado correctamente");
		}catch (Exception e) {
			redirectAttrs.addFlashAttribute("mensaje", "❌ Error al actualizar");
		}
		

	    return "redirect:/horarios/cargarHorario";
	}


	@PostMapping("/eliminar")
	public String eliminar(@RequestParam("id_horario") Integer id, RedirectAttributes redirectAttrs) {
	    try {
	        repo.eliminarHorario(id);
	        redirectAttrs.addFlashAttribute("mensaje", "🗑️ Horario eliminado correctamente");
	    } catch (Exception e) {
	        redirectAttrs.addFlashAttribute("mensaje", "❌ Error al eliminar el horario, Horario asignado");
	    }
	    return "redirect:/horarios/cargarHorario";
	}


}
