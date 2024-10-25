package com.application.rest.controllers;

import com.application.rest.controllers.dto.ProductDTO;
import com.application.rest.entities.Product;
import com.application.rest.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/product")
public class ProductController {

    @Autowired
    private IProductService _productService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll(){
        List<ProductDTO> listadoMapeado = _productService.findAll()
                .stream()
                .map(product -> ProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .maker(product.getMaker())
                        .build()).toList();
        return ResponseEntity.ok(listadoMapeado);
    }

    @GetMapping("/findById/{id}")
    public  ResponseEntity<?> findById(@PathVariable Long id){
        Optional<Product> productoOptional =  _productService.findById(id);
        if(productoOptional.isPresent()){
            Product producto = productoOptional.get();
            ProductDTO productoMapeado = ProductDTO.builder()
                    .id(producto.getId())
                    .name(producto.getName())
                    .price(producto.getPrice())
                    .maker(producto.getMaker())
                    .build();
            return ResponseEntity.ok(productoMapeado);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ningun elemento encontrado");
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody ProductDTO productDTO) throws URISyntaxException {
        if(productDTO.getName().isBlank()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Los campos no son validos");
        }
        Product producto = Product.builder()
                        .name(productDTO.getName())
                        .price(productDTO.getPrice())
                        .maker(productDTO.getMaker())
                        .build();
        _productService.save(producto);
        return ResponseEntity.created(new URI("/api/product/save")).build();
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        if(id != null){
            Optional<Product> productOptional = _productService.findById(id);
            if(productOptional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El producto a eliminar no fue encontrado");
            _productService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Producto eliminado correctamente");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Es necesario que introduzca el codigo del producto");
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductDTO productDTO){
        Optional<Product> productOptional = _productService.findById(id);
        if(productOptional.isPresent()){
            Product producto = productOptional.get();
            producto.setName(productDTO.getName());
            producto.setPrice(productDTO.getPrice());
            producto.setMaker(productDTO.getMaker());
            _productService.save(producto);
            return ResponseEntity.status(HttpStatus.OK).body("Producto actualizado correctamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El producto a actualizar no fue encontrado");
    }




}
