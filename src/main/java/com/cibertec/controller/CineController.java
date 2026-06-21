package com.cibertec.controller;

import com.cibertec.model.Pelicula;
import com.cibertec.repository.FuncionRepository;
import com.cibertec.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String listarPeliculas(Model model) {
        log.info("CU01: Listando todas las películas activas.");
        model.addAttribute("peliculas", peliculaRepository.findByEstado(1));
        return "pelicula/listado"; // Archivo: templates/pelicula/listado.html
    }

    @GetMapping("/pelicula/nuevo")
    public String formularioPelicula(Model model) {
        log.info("CU01: Cargando formulario de registro de película.");
        model.addAttribute("pelicula", new Pelicula());
        return "pelicula/nuevo"; // Archivo: templates/pelicula/nuevo.html
    }

    @PostMapping("/pelicula/guardar")
    public String guardarPelicula(@ModelAttribute("pelicula") Pelicula pelicula, Model model) {
        try {
            log.info("CU01: Guardando película: {}", pelicula.getTitulo());
            pelicula.setEstado(1); // Asegura el alta activa por defecto
            peliculaRepository.save(pelicula);
            return "redirect:/cine/peliculas";
        } catch (Exception e) {
            log.error("Error al guardar la película: {}", e.getMessage());
            model.addAttribute("error", "No se pudo registrar la película. Verifique los datos.");
            return "pelicula/nuevo";
        }
    }

    // ==========================================
    // 2. PROCESO TRANSACCIONAL: VENTAS (CU06)
    // ==========================================

    @GetMapping("/venta/nuevo")
    public String formularioVenta(Model model) {
        log.info("CU06: Cargando pantalla de registro de ventas.");
        // Lista las funciones vigentes a partir de la fecha actual
        model.addAttribute("funciones", funcionRepository.findByFechaGreaterThanEqual(LocalDate.now()));
        return "venta/nuevo"; // Archivo: templates/venta/nuevo.html
    }

    @PostMapping("/venta/guardar")
    public String procesarVenta(
            @RequestParam("idFuncion") Integer idFuncion,
            @RequestParam("cantidad") Integer cantidad,
            @RequestParam("asientos") String asientos,
            Model model) {

        log.info("CU06: Procesando venta transaccional. Función ID: {}, Entradas: {}", idFuncion, cantidad);

        try {
            // Simulamos el ID del usuario Taquillero que atiende la operación
            Integer idUsuarioTaquillero = 2; 

            // Invoca de manera segura al Stored Procedure de tu base de datos
            funcionRepository.registrarVentaCompleta(idUsuarioTaquillero, idFuncion, cantidad, asientos);
            
            log.info("Venta guardada con éxito en la base de datos.");
            model.addAttribute("mensajeExito", "¡Venta transaccional registrada correctamente!");
        } catch (Exception e) {
            log.error("Fallo crítico en el Stored Procedure de venta: {}", e.getMessage());
            model.addAttribute("mensajeError", "Error al procesar la venta: Capacidad de sala insuficiente o error de base de datos.");
        }

        // Recarga la lista de funciones actualizadas para la siguiente operación
        model.addAttribute("funciones", funcionRepository.findByFechaGreaterThanEqual(LocalDate.now()));
        return "venta/nuevo";
    }
}