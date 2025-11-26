package com.inventario.controller;

import com.inventario.repository.CompraRepository;
import com.inventario.repository.VentaRepository;
import com.inventario.repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private CompraRepository compraRepo;

    @Autowired
    private VentaRepository ventaRepo;

    @GetMapping
    public String vistaReportes(Model model) {

        long totalProductos = productoRepo.count();
        long totalCompras = compraRepo.count();
        long totalVentas = ventaRepo.count();

        double totalComprasMoney = compraRepo.findAll().stream()
                .mapToDouble(c -> c.getCantidad() * c.getCostoUnitario())
                .sum();

        double totalVentasMoney = ventaRepo.findAll().stream()
                .mapToDouble(v -> v.getCantidad() * v.getPrecioUnitario())
                .sum();

        double ganancia = totalVentasMoney - totalComprasMoney;

        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalCompras", totalCompras);
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("ganancia", ganancia);

        return "reportes/reportes";
    }
}
