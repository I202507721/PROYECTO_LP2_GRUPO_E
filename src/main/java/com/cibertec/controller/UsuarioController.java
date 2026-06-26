package com.cibertec.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cibertec.repository.TipoUsuarioRepository;
import com.cibertec.repository.UsuarioRepository;
import com.cibertec.util.Alert;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;

    // ==========================================
    // LISTADO DE USUARIOS (Solo ADMIN)
    // ==========================================
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, HttpSession session, RedirectAttributes flash) {
        if (!"Administrador".equals(session.getAttribute("rol"))) {
            flash.addFlashAttribute("alert", Alert.sweetAlertError("Acceso denegado. Solo administradores."));
            return "redirect:/dashboard";
        }
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("tipos", tipoUsuarioRepository.findAll());
        return "usuario/listado";
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR USUARIO (Toggle)
    // ==========================================
    @GetMapping("/usuario/toggle/{id}")
    public String toggleUsuario(@PathVariable Integer id, HttpSession session, RedirectAttributes flash) {
        if (!"Administrador".equals(session.getAttribute("rol"))) {
            flash.addFlashAttribute("alert", Alert.sweetAlertError("Acceso denegado."));
            return "redirect:/dashboard";
        }

        var usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) {
            flash.addFlashAttribute("alert", Alert.sweetAlertError("Usuario no encontrado."));
            return "redirect:/admin/usuarios";
        }

        // No permitir desactivar al propio admin logueado
        Integer idSesion = (Integer) session.getAttribute("idUsuario");
        if (usuario.getIdUsuario().equals(idSesion)) {
            flash.addFlashAttribute("alert", Alert.sweetAlertError("No puedes desactivarte a ti mismo."));
            return "redirect:/admin/usuarios";
        }

        // Invertir el estado
        usuario.setActivo(!usuario.getActivo());
        usuarioRepository.save(usuario);

        String msg = usuario.getActivo() ? "Usuario activado correctamente." : "Usuario desactivado correctamente.";
        flash.addFlashAttribute("alert", Alert.sweetAlertSuccess(msg));
        return "redirect:/admin/usuarios";
    }
}