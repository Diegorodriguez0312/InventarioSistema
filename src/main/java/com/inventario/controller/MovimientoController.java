package com.inventario.controller;

import com.inventario.model.Movimiento;
import com.inventario.service.MovimientoService;
import com.inventario.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String porProducto(@PathVariable Integer id, Model model) {
        try {
            model.addAttribute("movimientos", movimientoService.movimientosPorProducto(id));
            return "movimientos/lista";
        } catch (Exception e) {
            model.addAttribute("mensaje", "Producto no encontrado");
            return "movimientos/lista";
        }
    }

    // FORMULARIO NUEVO MOVIMIENTO
    @GetMapping("/nuevo")
    public String nuevoMovimiento(Model model) {
        model.addAttribute("movimiento", new Movimiento());
        model.addAttribute("productos", productoService.listarTodos());
        return "movimientos/form"; // <-- Asegúrate que exista este HTML
    }

    // GUARDAR MOVIMIENTO
    @PostMapping("/guardar")
    public String guardarMovimiento(@ModelAttribute Movimiento movimiento) {
        movimientoService.guardar(movimiento);
        return "redirect:/movimientos";
    }
}
