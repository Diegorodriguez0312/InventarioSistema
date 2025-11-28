package com.inventario.service;

import com.inventario.model.Producto;
import com.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository repository;

    // Listar todos
    public List<Producto> listarTodos() {
        return repository.findAll();
    }

    // Obtener por ID
    public Optional<Producto> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    // Crear
    public Producto crear(Producto p) throws Exception {
        // Validar
        if (p.getCodigo() == null || p.getCodigo().trim().length() < 3) {
            throw new Exception("Código debe tener al menos 3 caracteres");
        }
        if (p.getNombre() == null || p.getNombre().trim().length() < 5) {
            throw new Exception("Nombre debe tener al menos 5 caracteres");
        }
        if (p.getPrecio() == null || p.getPrecio() <= 0) {
            throw new Exception("Precio debe ser mayor a 0");
        }

        // Verificar código único
        if (repository.findByCodigo(p.getCodigo()).isPresent()) {
            throw new Exception("Código ya existe");
        }

        return repository.save(p);
    }

    public Producto actualizar(Integer id, Producto p) throws Exception {

        Producto producto = repository.findById(id)
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        // Validar código (solo si viene en el request)
        if (p.getCodigo() != null) {

            // Buscar cualquier producto con ese codigo
            Optional<Producto> encontrado = repository.findByCodigo(p.getCodigo());

            // Si existe y no es el mismo producto → error
            if (encontrado.isPresent() && !encontrado.get().getId().equals(id)) {
                throw new Exception("Código ya existe en otro producto");
            }

            producto.setCodigo(p.getCodigo());
        }

        // Actualizar otros campos
        if (p.getNombre() != null) producto.setNombre(p.getNombre());
        if (p.getPrecio() != null) producto.setPrecio(p.getPrecio());
        if (p.getStock() != null) producto.setStock(p.getStock());
        if (p.getActivo() != null) producto.setActivo(p.getActivo());

        return repository.save(producto);
    }



    // Eliminar
    public void eliminar(Integer id) throws Exception {
        if (!repository.existsById(id)) {
            throw new Exception("Producto no encontrado");
        }
        repository.deleteById(id);
    }

    public List<Producto> buscar(String texto) {
        return repository.findByNombreContainingIgnoreCaseOrCodigoContainingIgnoreCase(texto,texto);
    }
}
