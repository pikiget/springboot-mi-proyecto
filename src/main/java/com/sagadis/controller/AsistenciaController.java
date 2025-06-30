package com.sagadis.controller;

import java.io.OutputStream;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sagadis.model.Asistencia;
import com.sagadis.model.Usuario;
import com.sagadis.repository.IAsistenciaRepository;
import com.sagadis.repository.ITrabajadorRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaController {

    @SuppressWarnings("unused")
	private final TrabajadorController trabajadorController;
    @Autowired
    private IAsistenciaRepository repoAsis;
    
	@Autowired
	private ITrabajadorRepository repoTrab;

    AsistenciaController(TrabajadorController trabajadorController) {
        this.trabajadorController = trabajadorController;
    }

    @GetMapping("/cargarReporte")
    public String cargarReporte(Model model){
    	model.addAttribute("asistencias", new Asistencia());
    	model.addAttribute("lstAsistencia", repoAsis.findAll());
    	model.addAttribute("lstTrabajador", repoTrab.findAll());   
    	return "reporte";
    }
    
    @PersistenceContext
    private EntityManager entityManager;

    @org.springframework.transaction.annotation.Transactional
    @PostMapping("/registrarEntrada")
    public String registrarEntrada(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario != null && usuario.getId_trabajador() != null) {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("spU_Registrar_Marcacion_Mensaje");
            query.registerStoredProcedureParameter("p_id_trabajador", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

            query.setParameter("p_id_trabajador", usuario.getId_trabajador());
            query.execute();

            String mensaje = (String) query.getOutputParameterValue("p_mensaje");
            redirectAttributes.addFlashAttribute("mensaje", mensaje);
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "ERROR: Usuario no válido");
        }

        return "redirect:/usuarios/cargarMenu";
    }



    
    @Autowired
	private DataSource dataSource; // javax.sql

	@Autowired
	private ResourceLoader resourceLoader; // core.io

	@GetMapping("/reportes")
	public void reportes(HttpServletResponse response) {
	    // opción 1 DESCARGA UN PDF
	    //response.setHeader("Content-Disposition", "attachment; filename=\"reporte.pdf\";");
	    // opción 2 VISUALIZA EN LINEA 
	    response.setHeader("Content-Disposition", "inline;");
	    
	    response.setContentType("application/pdf");
	    try {
	        String ru = resourceLoader.getResource("classpath:/static/Cherry.jasper").getURI().getPath();
	        System.out.println("Ruta del reporte: " + ru);
	        
	        JasperPrint jasperPrint = JasperFillManager.fillReport(ru, null, dataSource.getConnection());
	        
	        if (jasperPrint == null) {
	            System.err.println("El jasperPrint es nulo.");
	            return;
	        }

	        OutputStream outStream = response.getOutputStream();
	        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	}


}