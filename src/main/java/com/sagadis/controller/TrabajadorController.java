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

import com.sagadis.model.Trabajador;
import com.sagadis.repository.IHorarioRespository;
import com.sagadis.repository.ITrabajadorRepository;

@Controller
@RequestMapping("/trabajadores")
public class TrabajadorController {
	
	@Autowired
	private IHorarioRespository repoHora;

	@Autowired
	private ITrabajadorRepository repoTrab;
	
	@GetMapping("/cargarTrabajador")
	public String cargarUsuario(Model model) {
		model.addAttribute("trabajadores", new Trabajador());
		model.addAttribute("lstTrabajador", repoTrab.findAll());
		model.addAttribute("lstHora", repoHora.findAll());
		return "crudTrabajador";
	}
	
	@PostMapping("/guardarTrabajador")
	public String guardarTrabajador(@ModelAttribute Trabajador trabajador,Model model, RedirectAttributes redirectAttrs) {

	    try {
	    	trabajador.setFecha_creacion(Date.valueOf(LocalDate.now()));
	        repoTrab.save(trabajador); // actualiza si ya existe
	        redirectAttrs.addFlashAttribute("mensaje", "✅ Trabajador registrado correctamente");
	    } catch (Exception e) {
	        redirectAttrs.addFlashAttribute("mensaje", "❌ Error al registrar");
	    }
	    return "redirect:/trabajadores/cargarTrabajador";
	}
	
	@GetMapping("/cargar/{id}")
	@ResponseBody
	public Trabajador cargarUsuarioPorId(@PathVariable("id") int id) {
	    return repoTrab.findById(id).orElse(null);
	}
	
	@PostMapping("/actualizar")
	public String actualizarTrabajador(@ModelAttribute Trabajador trabajador, RedirectAttributes redirectAttrs) {
	    try {
			trabajador.setFecha_modificacion(new Date(System.currentTimeMillis()));
		    repoTrab.save(trabajador);
		    redirectAttrs.addFlashAttribute("mensaje", "✅ Trabajador actualizado correctamente");
		} catch (Exception e) {
			redirectAttrs.addFlashAttribute("mensaje", "❌ Error al actualizar");
		}
	    return "redirect:/trabajadores/cargarTrabajador";
	}
	
	@PostMapping("/eliminar")
	public String eliminarTrabajador(@RequestParam("id_trabajador") Integer id, RedirectAttributes redirectAttrs) {
	    try {
	        repoTrab.eliminarTrabajador(id);
	        redirectAttrs.addFlashAttribute("mensaje", "✅ Trabajador eliminado correctamente");
	    } catch (Exception e) {
	        redirectAttrs.addFlashAttribute("mensaje", "❌ Error al eliminar usuario");
	    }
	    return "redirect:/trabajadores/cargarTrabajador";
	}
	
}
