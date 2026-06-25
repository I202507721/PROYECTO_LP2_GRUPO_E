package com.cibertec.controller;

import com.cibertec.model.Pelicula;
import com.cibertec.repository.FuncionRepository;
import com.cibertec.repository.PeliculaRepository;
import com.cibertec.util.Alert;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Slf4j
@Controller
@RequestMapping("/cine")
@RequiredArgsConstructor
public class CineController {

    private final PeliculaRepository peliculaRepository;
    private final FuncionRepository funcionRepository;

    // ==========================================
    // 1. MANTENIMIENTO: PELÍCULAS (CU01)
    // ==========================================

    @GetMapping("/peliculas")
    public String listarPeliculas(@RequestParam(name = "txtGenero", required = false) String genero, Model model) {
        if (genero != null && !genero.trim().isEmpty()) {
            log.info("CU01: Filtrando películas por género contenga: '{}'", genero);
            //Ejecutamos el procedimiento almacenado
            model.addAttribute("peliculas", peliculaRepository.filtrarPorGenero(genero));
            model.addAttribute("generoBuscado", genero);
        } else {
            log.info("CU01: Listando todas las películas activas por defecto.");
            model.addAttribute("peliculas", peliculaRepository.findByEstado(true));
        }
        return "pelicula/listado";
    }

    @GetMapping("/pelicula/nuevo")
    public String formularioPelicula(Model model) {
        model.addAttribute("pelicula", new Pelicula());
        return "pelicula/nuevo";
    }

    @PostMapping("/pelicula/guardar")
    public String guardarPelicula(@ModelAttribute("pelicula") Pelicula pelicula, Model model, RedirectAttributes flash) {
        try {
            pelicula.setEstado(true); 
            peliculaRepository.save(pelicula);
            
            // Usamos SweetAlert para una notificación elegante
            flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("Película registrada correctamente."));
            return "redirect:/cine/peliculas";
        } catch (Exception e) {
            log.error("Error al guardar la película: {}", e.getMessage());
            model.addAttribute("alert", Alert.sweetAlertError("No se pudo registrar la película. Verifique los datos."));
            return "pelicula/nuevo";
        }
    }

    // ==========================================
    // 2. PROCESO TRANSACCIONAL: VENTAS (CU06)
    // ==========================================

    @GetMapping("/venta/nuevo")
    public String formularioVenta(Model model, HttpSession session, RedirectAttributes flash) {
        // Validamos que nadie entre a taquilla copiando y pegando la URL sin loguearse
        if (session.getAttribute("idUsuario") == null) {
            flash.addFlashAttribute("alert", Alert.sweetAlertInfo("Debe iniciar sesión para acceder a la Taquilla"));
            return "redirect:/";
        }
        
        model.addAttribute("funciones", funcionRepository.findByFechaGreaterThanEqual(LocalDate.now()));
        return "venta/nuevo";
    }

    @PostMapping("/venta/guardar")
    public String procesarVenta(
            @RequestParam("idFuncion") Integer idFuncion,
            @RequestParam("cantidad") Integer cantidad,
            @RequestParam("asientos") String asientos,
            Model model,
            HttpSession session,
            RedirectAttributes flash) {

        // Capturamos el ID real del usuario desde la sesión activa
        Integer idUsuarioTaquillero = (Integer) session.getAttribute("idUsuario");
        
        if (idUsuarioTaquillero == null) {
            flash.addFlashAttribute("alert", Alert.sweetAlertInfo("Su sesión ha expirado"));
            return "redirect:/";
        }

        try {
            funcionRepository.registrarVentaCompleta(idUsuarioTaquillero, idFuncion, cantidad, asientos);
            
            // Enviamos el mensaje de éxito y redirigimos para limpiar el formulario
            flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("¡Venta transaccional procesada con éxito!"));
            return "redirect:/cine/venta/nuevo";
            
        } catch (Exception e) {
            log.error("Fallo crítico en el SP de venta: {}", e.getMessage());
            model.addAttribute("alert", Alert.sweetAlertError("Error en taquilla: Capacidad insuficiente o formato de asientos inválido."));
        }

        // Si hay error, recargamos la lista en la misma vista
        model.addAttribute("funciones", funcionRepository.findByFechaGreaterThanEqual(LocalDate.now()));
        return "venta/nuevo";
    }
}