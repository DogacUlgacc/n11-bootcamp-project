package com.dogac.product_service.infrastructure.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.dogac.product_service.domain.entities.Product;
import com.dogac.product_service.domain.repositories.ProductRepository;
import com.dogac.product_service.domain.valueobjects.Description;
import com.dogac.product_service.domain.valueobjects.ProductId;
import com.dogac.product_service.domain.valueobjects.ProductName;
import com.dogac.product_service.domain.valueobjects.StockQuantity;
import com.dogac.product_service.infrastructure.entities.JpaProductEntity;
import com.dogac.product_service.infrastructure.mapper.ProductEntityMapper;
import com.dogac.product_service.infrastructure.repositories.SpringDataProductRepository;

@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final SpringDataProductRepository springDataProductRepository;
    private final ProductEntityMapper productEntityMapper;

    public ProductRepositoryAdapter(SpringDataProductRepository springDataProductRepository,
            ProductEntityMapper productEntityMapper) {
        this.springDataProductRepository = springDataProductRepository;
        this.productEntityMapper = productEntityMapper;
    }

    @Override
    public Product save(Product product) {
        var entity = productEntityMapper.toEntity(product);
        entity = springDataProductRepository.save(entity);
        return productEntityMapper.toDomain(entity);
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return springDataProductRepository.findById(id.value()).map(productEntityMapper::toDomain);

    }

    @Override
    public List<Product> findAll() {
        return springDataProductRepository.findAll().stream().map(productEntityMapper::toDomain).toList();

    }

    @Override
    public void deleteById(ProductId id) {
        springDataProductRepository.deleteById(id.value());
    }

    @Override
    public boolean existsById(ProductId id) {
        return springDataProductRepository.existsById(id.value());
    }

    @Override
    public boolean existsByName(String name) {
        return springDataProductRepository.existsByProductName(name);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {

        Page<JpaProductEntity> entityPage = springDataProductRepository.findAll(pageable);

        return entityPage.map(this::toDomain);
    }

    private Product toDomain(JpaProductEntity entity) {
        return new Product(
                new ProductId(entity.getId()),
                new ProductName(entity.getProductName()),
                new Description(entity.getProductDescription()),
                entity.getPrice(), // Zaten Money nesnesi ise doğrudan verilir
                new StockQuantity(entity.getStockQuantity()));
    }

}
