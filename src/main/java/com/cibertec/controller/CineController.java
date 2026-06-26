package com.cibertec.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cibertec.model.Funcion;
import com.cibertec.model.Pelicula;
import com.cibertec.repository.FuncionRepository;
import com.cibertec.repository.PeliculaRepository;
import com.cibertec.repository.SalaRepository;
import com.cibertec.util.Alert;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Controller
@RequestMapping("/cine")
@RequiredArgsConstructor
public class CineController {

	private final PeliculaRepository peliculaRepository;
    private final FuncionRepository funcionRepository;
    private final SalaRepository salaRepository;
    // ==========================================
    // 1. MANTENIMIENTO: PELÍCULAS (CU01)
    // ==========================================

    @GetMapping("/peliculas")
    public String listarPeliculas(@RequestParam(name = "txtGenero", required = false) String genero, Model model) {
        if (genero != null && !genero.trim().isEmpty()) {
            model.addAttribute("peliculas", peliculaRepository.filtrarPorGenero(genero));
            model.addAttribute("generoBuscado", genero);
        } else {
            model.addAttribute("peliculas", peliculaRepository.findByEstado(true));
        }
        return "pelicula/listado";
    }

    //Enviamos la lista de las 12 salas a la vista
    @GetMapping("/pelicula/nuevo")
    public String formularioPelicula(Model model) {
        log.info("CU01: Cargando formulario de registro con listado de salas.");
        model.addAttribute("pelicula", new Pelicula());
        model.addAttribute("salas", salaRepository.findAll());
        return "pelicula/nuevo";
    }

    // GUARDADO AUTOMATIZADO: Guarda película y genera su primera función
    @PostMapping("/pelicula/guardar")
    public String guardarPelicula(
            @ModelAttribute("pelicula") Pelicula pelicula, 
            @RequestParam("idSala") Integer idSala,
            @RequestParam("fechaFuncion") LocalDate fechaFuncion,
            @RequestParam("horaFuncion") LocalTime horaFuncion, 
            Model model, 
            RedirectAttributes flash) {
        try {
            log.info("CU01: Iniciando registro de película y automatización de función.");

            pelicula.setEstado(true); 
            Pelicula peliculaRegistrada = peliculaRepository.save(pelicula);

            var salaSeleccionada = salaRepository.findById(idSala).orElseThrow();
   
            Funcion nuevaFuncion = new Funcion();
            nuevaFuncion.setPelicula(peliculaRegistrada);
            nuevaFuncion.setSala(salaSeleccionada);
            nuevaFuncion.setFecha(fechaFuncion); 
            nuevaFuncion.setHoraInicio(horaFuncion); 
            nuevaFuncion.setPrecioEntrada(salaSeleccionada.getPrecioBase());
            nuevaFuncion.setAsientosDisponibles(salaSeleccionada.getCapacidad());
            
            funcionRepository.save(nuevaFuncion); 
            
            flash.addFlashAttribute("alert", Alert.sweetAlertSuccess(
                "Película registrada. Función programada para el " + fechaFuncion + " a las " + horaFuncion));
            return "redirect:/cine/peliculas";
        } catch (Exception e) {
            log.error("Error en el flujo combinado de registro: {}", e.getMessage());
            model.addAttribute("salas", salaRepository.findAll()); 
            model.addAttribute("alert", Alert.sweetAlertError("No se pudo procesar el registro. Verifique los campos."));
            return "pelicula/nuevo";
        }
    }

 // ==========================================
    // 2. PROCESO TRANSACCIONAL: VENTAS (CU06)
    // ==========================================

    @GetMapping("/venta/nuevo")
    public String formularioVenta(Model model, HttpSession session, RedirectAttributes flash) {
        if (session.getAttribute("idUsuario") == null) {
            flash.addFlashAttribute("alert", Alert.sweetAlertInfo("Debe iniciar sesión para acceder a la Taquilla"));
            return "redirect:/";
        }
        
        // AHORA MANDAMOS LA LISTA DE PELÍCULAS AL PRIMER COMBO
        model.addAttribute("peliculas", peliculaRepository.findByEstado(true));
        return "venta/nuevo";
    }

    @GetMapping("/venta/funciones-por-pelicula")
    @ResponseBody
    public List<Map<String, Object>> obtenerFunciones(@RequestParam("idPelicula") Integer idPelicula) {
        List<Funcion> funciones = funcionRepository.findActivasByPelicula(idPelicula);
        List<Map<String, Object>> response = new ArrayList<>();
        
        for (Funcion f : funciones) {
            Map<String, Object> map = new HashMap<>();
            map.put("idFuncion", f.getIdFuncion());
            map.put("precio", f.getPrecioEntrada());
            
            String texto = "Sala " + f.getSala().getNumeroSala() + " - " + f.getFecha() + " a las " + f.getHoraInicio() + 
                           " (S/. " + f.getPrecioEntrada() + ") - Libres: " + f.getAsientosDisponibles();
            map.put("texto", texto);
            response.add(map);
        }
        return response;
    }

    @PostMapping("/venta/guardar")
    public String procesarVenta(
            @RequestParam("idFuncion") Integer idFuncion,
            @RequestParam("cantidad") Integer cantidad,
            @RequestParam("asientos") String asientos,
            Model model,
            HttpSession session,
            RedirectAttributes flash) {

        Integer idUsuarioTaquillero = (Integer) session.getAttribute("idUsuario");
        
        if (idUsuarioTaquillero == null) {
            flash.addFlashAttribute("alert", Alert.sweetAlertInfo("Su sesión ha expirado"));
            return "redirect:/";
        }

        try {
            funcionRepository.registrarVentaCompleta(idUsuarioTaquillero, idFuncion, cantidad, asientos);
            flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("¡Venta transaccional procesada con éxito!"));
            return "redirect:/cine/venta/nuevo";
            
        } catch (Exception e) {
            log.error("Fallo crítico en el SP de venta: {}", e.getMessage());
            model.addAttribute("alert", Alert.sweetAlertError("Error en taquilla: Capacidad insuficiente o código de asientos inválido."));
        }

        // Si hay error, recargamos la lista de películas para que vuelva a intentar
        model.addAttribute("peliculas", peliculaRepository.findByEstado(true));
        return "venta/nuevo";
    }
}