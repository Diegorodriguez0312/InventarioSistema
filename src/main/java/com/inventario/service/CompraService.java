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

import java.util.List;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private MovimientoRepository movimientoRepo;

    // -----------------------------------------------------------
    // CREAR COMPRA
    // -----------------------------------------------------------
    @Transactional
    public Compra registrarCompra(Compra compra) throws Exception {

        Producto producto = productoRepo.findById(compra.getProducto().getId())
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        if (compra.getCantidad() == null || compra.getCantidad() <= 0)
            throw new Exception("Cantidad debe ser mayor a 0");

        if (compra.getCostoUnitario() == null || compra.getCostoUnitario() <= 0)
            throw new Exception("Costo unitario inválido");

        int nuevoStock = (producto.getStock() == null ? 0 : producto.getStock()) + compra.getCantidad();
        producto.setStock(nuevoStock);
        productoRepo.save(producto);

        compra.setProducto(producto);
        Compra saved = compraRepo.save(compra);

        // Registrar movimiento
        registrarMovimiento(producto, compra.getCantidad(), nuevoStock, "ENTRADA",
                "COMPRA:" + saved.getId(), "Compra registrada");

        return saved;
    }

    // -----------------------------------------------------------
    // ACTUALIZAR COMPRA
    // -----------------------------------------------------------
    @Transactional
    public Compra actualizarCompra(Integer id, Compra nueva) throws Exception {

        Compra actual = compraRepo.findById(id)
                .orElseThrow(() -> new Exception("Compra no encontrada"));

        Producto producto = productoRepo.findById(nueva.getProducto().getId())
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        // Revertir stock anterior
        int stockRevertido = producto.getStock() - actual.getCantidad();

        // Aplicar la nueva cantidad
        int stockFinal = stockRevertido + nueva.getCantidad();

        if (stockFinal < 0)
            throw new Exception("Stock final no puede ser negativo");

        producto.setStock(stockFinal);
        productoRepo.save(producto);

        // Actualizar campos
        actual.setProducto(producto);
        actual.setCantidad(nueva.getCantidad());
        actual.setCostoUnitario(nueva.getCostoUnitario());

        compraRepo.save(actual);

        registrarMovimiento(producto, nueva.getCantidad(), stockFinal, "ENTRADA",
                "COMPRA-ACT:" + id, "Actualización de compra");

        return actual;
    }

    // -----------------------------------------------------------
    // REGISTRAR MOVIMIENTO
    // -----------------------------------------------------------
    private void registrarMovimiento(Producto producto, int cantidad, int stockFinal,
                                     String tipo, String referencia, String detalle) {

        Movimiento m = new Movimiento();
        m.setProducto(producto);
        m.setTipo(tipo);
        m.setCantidad(cantidad);
        m.setReferencia(referencia);
        m.setStockFinal(stockFinal);
        m.setDetalle(detalle);
        movimientoRepo.save(m);
    }

    // -----------------------------------------------------------
    // LISTAR HISTORIAL
    // -----------------------------------------------------------
    public List<Compra> historialCompras() {
        return compraRepo.findAll();
    }

    // -----------------------------------------------------------
    // OBTENER POR ID
    // -----------------------------------------------------------
    public Compra buscarPorId(Integer id) {
        return compraRepo.findById(id).orElse(null);
    }

    // -----------------------------------------------------------
    // ELIMINAR
    // -----------------------------------------------------------
    @Transactional
    public void eliminar(Integer id) throws Exception {

        Compra compra = compraRepo.findById(id)
                .orElseThrow(() -> new Exception("Compra no encontrada"));

        Producto p = compra.getProducto();

        // revertir stock
        int stockFinal = p.getStock() - compra.getCantidad();
        if (stockFinal < 0) stockFinal = 0;

        p.setStock(stockFinal);
        productoRepo.save(p);

        registrarMovimiento(p, compra.getCantidad(), stockFinal,
                "SALIDA", "COMPRA-DEL:" + id, "Eliminar compra");

        compraRepo.deleteById(id);
    }
}
