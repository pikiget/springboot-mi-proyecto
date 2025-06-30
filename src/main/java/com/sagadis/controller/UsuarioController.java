package com.sagadis.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import com.sagadis.model.Usuario;
import com.sagadis.repository.IRolRepository;
import com.sagadis.repository.ITrabajadorRepository;
import com.sagadis.repository.IUsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
	@Autowired
	private IRolRepository repoRol;
	
	@Autowired
	private IUsuarioRepository repoUser;
	
	@Autowired
	private ITrabajadorRepository  repoTrab;
	
	@GetMapping("/cargarLogin")
	public String cargarLogin() {
		return "login" ;
	}
	
	@GetMapping("/cargarMenu")
	public String cargarMenu() {
	    return "menu";
	}

	
	@PostMapping("/validarLogin")
	public String validarLogin(
	    @RequestParam(required = false) String username,
	    @RequestParam(required = false) String clave,
	    Model model,
	    HttpSession session
	) {
	    if (username != null && clave != null) {

	        Optional<Usuario> user = repoUser.findByUsernameAndContrasenia(username, clave);

	        if (user.isPresent()) {
	        	session.setAttribute("usuarioLogueado", user.get());
	        	model.addAttribute("usuario", user.get());


	            model.addAttribute("mensaje", "Bienvenido, " + user.get().getUsername());
	            model.addAttribute("cssmensaje", "alert alert-success");
	            
	            return "redirect:/usuarios/cargarMenu"; // redirige al menú Thymeleaf
	        } else {
	            model.addAttribute("mensaje", "Usuario o contraseña incorrectos");
	            model.addAttribute("cssmensaje", "alert alert-danger");
	            return "login";  // ⛔ Vuelve al login si está mal
	        }

	    } else {
	        model.addAttribute("mensaje", "Debes ingresar usuario y contraseña");
	        model.addAttribute("cssmensaje", "alert alert-warning");
	        return "login";
	    }
	}

	
	
	@GetMapping("/cargarUsuario")
	public String cargarUsuario(Model model) {
		model.addAttribute("usuarios", new Usuario());
		
		List<Trabajador> trabajadoresSinUsuario = repoTrab.listarTrabajadoresActivosSinUsuario();
		model.addAttribute("lstTrabajadores", trabajadoresSinUsuario);	
		
		model.addAttribute("lstTrabajador", repoTrab.findAll());
		model.addAttribute("lstUsuario", repoUser.findAll());
		model.addAttribute("lstRol", repoRol.findAll());
		return "crudUsuario";
	}
	
	@PostMapping("/guardarUsuario")
	public String guardarUsuario(@ModelAttribute Usuario usuario,Model model, RedirectAttributes redirectAttrs) {

		try {
			//VERIFICA SI EL USERNAME EXISTE PO
			Optional<Usuario> existente = repoUser.findByUsername(usuario.getUsername());
			
			if(existente.isPresent()){
				redirectAttrs.addFlashAttribute("mensaje", "❌ El nombre del usuario ya existe");
			}else {
				usuario.setFecha_creacion(Date.valueOf(LocalDate.now()));
	            repoUser.save(usuario);
	            redirectAttrs.addFlashAttribute("mensaje", "✅ Usuario registrado correctamente");
			}
			
		} catch (Exception e) {
	        redirectAttrs.addFlashAttribute("mensaje", "❌ Error al registrar");
	        
		}
		return "redirect:/usuarios/cargarUsuario";
	}
	
	@GetMapping("/cargar/{id}")
	@ResponseBody
	public Usuario cargarUsuarioPorId(@PathVariable("id") int id) {
	    return repoUser.findById(id).orElse(null);
	}
	
	@PostMapping("/actualizar")
	public String actualizarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttrs) {
	    try {
	    	usuario.setFecha_modificacion(Date.valueOf(LocalDate.now()));
	        repoUser.save(usuario); // actualiza si ya existe
	        redirectAttrs.addFlashAttribute("mensaje", "✅ Usuario actualizado correctamente");
	    } catch (Exception e) {
	        redirectAttrs.addFlashAttribute("mensaje", "❌ Error al actualizar");
	    }
	    return "redirect:/usuarios/cargarUsuario";
	}
	
	@PostMapping("/eliminar")
	public String eliminarUsuario(@RequestParam("id_usuario") Integer id, RedirectAttributes redirectAttrs) {
	    try {
	        repoUser.eliminarUsuario(id);
	        redirectAttrs.addFlashAttribute("mensaje", "✅ Usuario eliminado correctamente");
	    } catch (Exception e) {
	        redirectAttrs.addFlashAttribute("mensaje", "❌ Error al eliminar usuario");
	    }
	    return "redirect:/usuarios/cargarUsuario";
	}


	@GetMapping("/logout")
	public String cerrarSesion(HttpSession session) {
	    session.invalidate(); // Elimina todo
	    return "redirect:/usuarios/cargarLogin";
	}

}
