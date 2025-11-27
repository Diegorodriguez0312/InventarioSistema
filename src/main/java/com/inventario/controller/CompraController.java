package com.inventario.controller;

import com.inventario.model.Compra;
import com.inventario.service.CompraService;
import com.inventario.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @Autowired
    private ProductoService productoService;

    // LISTA
    @GetMapping
    public String listado(Model model) {
        model.addAttribute("compras", compraService.historialCompras());
        return "compras/lista";
    }

    // NUEVA COMPRA
    @GetMapping("/nueva")
    public String nuevoForm(Model model) {
        model.addAttribute("compra", new Compra());
        model.addAttribute("productos", productoService.listarTodos());
        return "compras/form";
    }

    // REGISTRAR
    @PostMapping
    public String registrar(@ModelAttribute Compra compra, RedirectAttributes redirect) {
        try {
            compraService.registrarCompra(compra);
            redirect.addFlashAttribute("mensaje", "Compra registrada correctamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }
        return "redirect:/compras";
    }

    // EDITAR
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") Integer id,
                         Model model,
                         RedirectAttributes redirect) {

        Compra compra = compraService.buscarPorId(id);

        if (compra == null) {
            redirect.addFlashAttribute("mensaje", "La compra no existe");
            redirect.addFlashAttribute("tipo", "error");
            return "redirect:/compras";
        }

        model.addAttribute("compra", compra);
        model.addAttribute("productos", productoService.listarTodos());
        return "compras/form";
    }

    // ACTUALIZAR
    @PostMapping("/{id}")
    public String actualizar(@PathVariable("id") Integer id,
                             @ModelAttribute Compra compra,
                             RedirectAttributes redirect) {

        try {
            compraService.actualizarCompra(id, compra); // CORREGIDO
            redirect.addFlashAttribute("mensaje", "Compra actualizada correctamente");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error al actualizar: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }

        return "redirect:/compras";
    }

    // ELIMINAR
    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        try {
            compraService.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Compra eliminada");
            redirect.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            redirect.addFlashAttribute("tipo", "error");
        }

        return "redirect:/compras";
    }
}
