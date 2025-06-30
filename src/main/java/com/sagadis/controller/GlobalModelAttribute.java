package com.sagadis.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalModelAttribute {
    @ModelAttribute
    public void agregarUsuarioLogueadoAlModelo(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            model.addAttribute("usuarioLogueado", usuario);
        }
    }
}
