package com.inventario.controller;

import com.inventario.model.Venta;
import com.inventario.service.ProductoService;
import com.inventario.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoService productoService;

    // LISTA
    @GetMapping
    public String listado(Model model) {
        model.addAttribute("ventas", ventaService.historialVentas());
        return "ventas/lista";
    }

    // NUEVA VENTA
    @GetMapping("/nueva")
    public String nuevoForm(Model model) {
        model.addAttribute("venta", new Venta());
        model.addAttribute("productos", productoService.listarTodos());
        return "ventas/form";
    }

    // REGISTRAR
    @PostMapping
    public String registrar(@ModelAttribute Venta venta, RedirectAttributes redirect) {
        try {
            ventaService.registrarVenta(venta);
            redirect.addFlashAttribute("mensaje", "Venta registrada correctamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/ventas";
    }

    // EDITAR (opcional más adelante)
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        Venta venta = ventaService.buscarPorId(id);
        model.addAttribute("venta", venta);
        model.addAttribute("productos", productoService.listarTodos());
        return "ventas/form";
    }

    // ACTUALIZAR
    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable Integer id,
            @ModelAttribute Venta venta,
            RedirectAttributes redirect
    ) {
        try {
            venta.setId(id);
            ventaService.registrarVenta(venta);
            redirect.addFlashAttribute("mensaje", "Venta actualizada correctamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error al actualizar: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }

        return "redirect:/ventas";
    }

    // ELIMINAR
    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            ventaService.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Venta eliminada");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/ventas";
    }
}
