package com.inventario.controller;

import com.inventario.model.Movimiento;
import com.inventario.service.MovimientoService;
import com.inventario.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private ProductoService productoService;

    // LISTADO GENERAL
    @GetMapping
    public String listado(Model model) {
        model.addAttribute("movimientos", movimientoService.listarTodos());
        return "movimientos/lista";
    }

    // LISTAR POR PRODUCTO
    @GetMapping("/producto/{id}")
    public String porProducto(@PathVariable Integer id,
                              Model model,
                              RedirectAttributes redirect) {
        try {
            model.addAttribute("movimientos", movimientoService.movimientosPorProducto(id));
            return "movimientos/lista";

        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Producto no encontrado");
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/movimientos";
        }
    }

    // FORMULARIO NUEVO MOVIMIENTO
    @GetMapping("/nuevo")
    public String nuevoMovimiento(Model model) {
        model.addAttribute("movimiento", new Movimiento());
        model.addAttribute("productos", productoService.listarTodos());
        return "movimientos/form";
    }

    // GUARDAR MOVIMIENTO
    @PostMapping("/guardar")
    public String guardarMovimiento(@ModelAttribute Movimiento movimiento,
                                    RedirectAttributes redirect) {

        try {
            movimientoService.guardar(movimiento);
            redirect.addFlashAttribute("mensaje", "Movimiento registrado correctamente");
            redirect.addFlashAttribute("tipo", "success");

        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error al guardar: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }

        return "redirect:/movimientos";
    }
}
