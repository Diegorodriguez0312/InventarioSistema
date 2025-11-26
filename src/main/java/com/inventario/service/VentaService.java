package com.inventario.service;

import com.inventario.model.Movimiento;
import com.inventario.model.Producto;
import com.inventario.model.Venta;
import com.inventario.repository.MovimientoRepository;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private MovimientoRepository movimientoRepo;

    // -----------------------------------------------------------------
    // REGISTRAR VENTA (restar stock y guardar movimiento)
    // -----------------------------------------------------------------
    @Transactional
    public Venta registrarVenta(Venta venta) throws Exception {

        Producto producto = productoRepo.findById(venta.getProducto().getId())
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        if (venta.getCantidad() == null || venta.getCantidad() <= 0) {
            throw new Exception("Cantidad debe ser mayor a 0");
        }
        if (venta.getPrecioUnitario() == null || venta.getPrecioUnitario() <= 0) {
            throw new Exception("Precio unitario inválido");
        }

        // VALIDACIÓN DE STOCK
        int stockActual = producto.getStock() == null ? 0 : producto.getStock();
        if (venta.getCantidad() > stockActual) {
            throw new Exception("No se puede vender más del stock disponible: " + stockActual);
        }

        // Restar stock
        int nuevoStock = stockActual - venta.getCantidad();
        producto.setStock(nuevoStock);
        productoRepo.save(producto);

        // Guardar venta
        venta.setProducto(producto);
        Venta saved = ventaRepo.save(venta);

        // Registrar movimiento de salida
        Movimiento m = new Movimiento();
        m.setProducto(producto);
        m.setTipo("SALIDA");
        m.setCantidad(venta.getCantidad());
        m.setReferencia("VENTA:" + saved.getId());
        m.setStockFinal(nuevoStock);
        m.setDetalle("Venta registrada");

        movimientoRepo.save(m);

        return saved;
    }

    // LISTAR TODAS
    public java.util.List<Venta> historialVentas() {
        return ventaRepo.findAll();
    }

    // BUSCAR POR ID
    public Venta buscarPorId(Integer id) {
        return ventaRepo.findById(id).orElse(null);
    }

    // ELIMINAR VENTA
    @Transactional
    public void eliminar(Integer id) throws Exception {
        ventaRepo.deleteById(id);
    }
}
