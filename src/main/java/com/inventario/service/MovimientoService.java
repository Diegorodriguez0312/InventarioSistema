package com.inventario.service;

import com.inventario.model.Movimiento;
import com.inventario.model.Producto;
import com.inventario.repository.MovimientoRepository;
import com.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepo;

    @Autowired
    private ProductoRepository productoRepo;

    // GUARDAR MOVIMIENTO Y ACTUALIZAR STOCK
    public void guardar(Movimiento m) {

        // Buscar producto real
        Producto producto = productoRepo.findById(m.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Ajustar stock según el tipo
        if ("ENTRADA".equalsIgnoreCase(m.getTipo())) {
            producto.setStock(producto.getStock() + m.getCantidad());
        }
        else if ("SALIDA".equalsIgnoreCase(m.getTipo())) {

            if (producto.getStock() < m.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para registrar salida");
            }

            producto.setStock(producto.getStock() - m.getCantidad());
        }

        // Guardar actualización del producto
        productoRepo.save(producto);

        // Registrar el movimiento
        movimientoRepo.save(m);
    }

    public List<Movimiento> movimientosPorProducto(Integer productoId) throws Exception {
        Producto p = productoRepo.findById(productoId)
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        return movimientoRepo.findByProductoOrderByFechaDesc(p);
    }

    public List<Movimiento> listarTodos() {
        return movimientoRepo.findAll();
    }
}
