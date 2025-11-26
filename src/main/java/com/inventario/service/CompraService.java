package com.inventario.service;

import com.inventario.model.Compra;
import com.inventario.model.Movimiento;
import com.inventario.model.Producto;
import com.inventario.repository.CompraRepository;
import com.inventario.repository.MovimientoRepository;
import com.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private MovimientoRepository movimientoRepo;

    // -----------------------------------------------------------------
    // REGISTRAR COMPRA (con actualización de stock y movimiento)
    // -----------------------------------------------------------------
    @Transactional
    public Compra registrarCompra(Compra compra) throws Exception {

        Producto producto = productoRepo.findById(compra.getProducto().getId())
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        if (compra.getCantidad() == null || compra.getCantidad() <= 0) {
            throw new Exception("Cantidad debe ser mayor a 0");
        }
        if (compra.getCostoUnitario() == null || compra.getCostoUnitario() <= 0) {
            throw new Exception("Costo unitario inválido");
        }

        // Actualizar stock del producto
        int nuevoStock = (producto.getStock() == null ? 0 : producto.getStock()) + compra.getCantidad();
        producto.setStock(nuevoStock);
        productoRepo.save(producto);

        // Guardar compra
        compra.setProducto(producto);
        Compra saved = compraRepo.save(compra);

        // Registrar movimiento de entrada
        Movimiento m = new Movimiento();
        m.setProducto(producto);
        m.setTipo("ENTRADA");
        m.setCantidad(compra.getCantidad());
        m.setReferencia("COMPRA:" + saved.getId());
        m.setStockFinal(nuevoStock);
        m.setDetalle("Compra registrada. Proveedor: " +
                (compra.getProveedor() == null ? "" : compra.getProveedor()));

        movimientoRepo.save(m);

        return saved;
    }

    // -----------------------------------------------------------------
    // LISTADO / HISTORIAL DE COMPRAS
    // -----------------------------------------------------------------
    public java.util.List<Compra> historialCompras() {
        return compraRepo.findAll();
    }

    // -----------------------------------------------------------------
    // BUSCAR COMPRA POR ID
    // -----------------------------------------------------------------
    public Compra buscarPorId(Integer id) {
        return compraRepo.findById(id)
                .orElse(null);
    }

    // -----------------------------------------------------------------
    // ELIMINAR COMPRA POR ID
    // -----------------------------------------------------------------
    @Transactional
    public void eliminar(Integer id) {
        compraRepo.deleteById(id);
    }
}