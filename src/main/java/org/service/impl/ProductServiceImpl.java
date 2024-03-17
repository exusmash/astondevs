package org.service.impl;

import org.dto.ProductIncomingDTO;
import org.dto.ProductOutGoingDTO;
import org.dto.ProductUpdateDTO;
import org.exeption.NotFoundException;
import org.mapper.ProductDTOMapper;
import org.mapper.impl.ProductDTOMapperImpl;
import org.model.EmployeeToProduct;
import org.model.Product;
import org.repository.EmployeeRepository;
import org.repository.EmployeeToProductRepository;
import org.repository.ProductRepository;
import org.repository.impl.EmployeeRepositoryImpl;
import org.repository.impl.EmployeeToProductRepositoryImpl;
import org.repository.impl.ProductRepositoryImpl;
import org.service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository = ProductRepositoryImpl.getInstance();
    private final EmployeeRepository employeeRepository = EmployeeRepositoryImpl.getInstance();
    private final EmployeeToProductRepository employeeToProductRepository = EmployeeToProductRepositoryImpl.getInstance();
    private static final ProductDTOMapper productDTOMapper = ProductDTOMapperImpl.getInstance();
    private static ProductService instance;


    private ProductServiceImpl() {
    }

    public static synchronized ProductService getInstance() {
        if (instance == null) {
            instance = new ProductServiceImpl();
        }
        return instance;
    }

    private void checkExistProduct(Long productId) throws NotFoundException {
        if (!productRepository.exitsById(productId)) {
            throw new NotFoundException("Product not found.");
        }
    }

    @Override
    public ProductOutGoingDTO save(ProductIncomingDTO productDTO) {
        Product product = productDTOMapper.map(productDTO);
        product = productRepository.save(product);
        return productDTOMapper.map(product);
    }

    @Override
    public void update(ProductUpdateDTO productUpdateDTO) throws NotFoundException {
        checkExistProduct(productUpdateDTO.getId());
        Product product = productDTOMapper.map(productUpdateDTO);
        productRepository.update(product);
    }

    @Override
    public ProductOutGoingDTO findById(Long productId) throws NotFoundException {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new NotFoundException("Product not found."));
        return productDTOMapper.map(product);
    }

    @Override
    public List<ProductOutGoingDTO> findAll() {
        List<Product> productList = productRepository.findAll();
        return productDTOMapper.map(productList);
    }

    @Override
    public void delete(Long productId) throws NotFoundException {
        checkExistProduct(productId);
        productRepository.deleteById(productId);
    }

    @Override
    public void deleteEmployeeFromProduct(Long productId, Long employeeId) throws NotFoundException {
        checkExistProduct(productId);
        if (employeeRepository.exitsById(employeeId)) {
            EmployeeToProduct linkEmployeeProduct = employeeToProductRepository.findByEmployeeIdAndProductId(employeeId, productId)
                    .orElseThrow(() -> new NotFoundException("Link many to many Not found."));

            employeeToProductRepository.deleteById(linkEmployeeProduct.getId());
        } else {
            throw new NotFoundException("Employee not found.");
        }

    }

    @Override
    public void addEmployeeToProduct(Long productId, Long employeeId) throws NotFoundException {
        checkExistProduct(productId);
        if (employeeRepository.exitsById(employeeId)) {
            EmployeeToProduct linkEmployeeProduct = new EmployeeToProduct(
                    null,
                    employeeId,
                    productId
            );
            employeeToProductRepository.save(linkEmployeeProduct);
        } else {
            throw new NotFoundException("Employee not found.");
        }

    }
}
