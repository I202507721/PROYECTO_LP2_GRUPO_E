package com.cibertec.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cibertec.dto.AutenticacionFilter;
import com.cibertec.service.AutenticacionService;
import com.cibertec.util.Alert;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private final AutenticacionService autenticacionService;

    @GetMapping("/iniciar-sesion")
    public String iniciarSesion(@ModelAttribute AutenticacionFilter filter, Model model, RedirectAttributes flash,
            HttpSession session) {

        var usuario = autenticacionService.login(filter);

        if (usuario == null) {
            var mensaje = Alert.sweetAlertError("Usuario y/o clave incorrectos");
            model.addAttribute("alert", mensaje);
            model.addAttribute("filter", filter);
            return "login";
        }
        
     // Validación del estado activo (Si no está activo...)
        if (!usuario.getActivo()) {
            var mensaje = Alert.sweetAlertError("Su cuenta se encuentra inactiva. Contacte al administrador.");
            model.addAttribute("alert", mensaje);
            model.addAttribute("filter", filter);
            return "login";
        }
        
        // Guardamos los datos del usuario en la sesión del servidor HTTP
        session.setAttribute("idUsuario", usuario.getIdUsuario());
        session.setAttribute("fullName", usuario.getNombre());
        session.setAttribute("rol", usuario.getRol());
        
        String mensajeBienvenida = usuario.getNombre() + " - " + usuario.getRol();
        
        String alert = Alert.sweetImageUrl("Bienvenido a CineMax", mensajeBienvenida, "/imagenes/mapache_pedro.gif");
        flash.addFlashAttribute("alert", alert);
        return "redirect:/dashboard";
        
    }
    
    @GetMapping("/cerrar-sesion")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}