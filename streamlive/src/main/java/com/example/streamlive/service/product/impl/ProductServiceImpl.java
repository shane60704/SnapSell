package com.example.streamlive.service.product.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.streamlive.dao.product.ProductDao;
import com.example.streamlive.dto.product.ProductDto;
import com.example.streamlive.model.Product;
import com.example.streamlive.model.user.ClientProduct;
import com.example.streamlive.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Value("${aws.s3.baseurl}")
    private String s3BaseUrl;

    private final AmazonS3 s3Client;
    private final ProductDao productDao;

    @Override
    public boolean listProduct(ProductDto productDto) {
        try {
            String mainImagePath = "111";
//            String mainImagePath = uploadProductImage(productDto.getMainImage());
            String feature = String.join(",", productDto.getFeature());
            Integer productId = productDao.createProduct(productDto,mainImagePath,feature);
            if (productId == null) {
                return false;
            }
            return productDao.createDelegation(productId, productDto.getUserId()) != null;
//        } catch (IOException e) {
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public List<ClientProduct> getProductsForDelegation(Long userId, int status,String sortBy,String sortOrder){
        return productDao.findProductsForDelegation(userId,status,sortBy,sortOrder);
    }

    @Override
    public List<Product> getDelegatedProducts(int userId){
        return productDao.findDelegatedProducts(userId);
    }

    @Override
    public List<Product> getUndelegatedProducts(int userId){
        return productDao.findUndelegatedProducts(userId);
    }

    @Override
    public List<Product> getMyDelegatedProducts(int userId){
        return productDao.findMyDelegatedProducts(userId);
    }

    @Override
    public Product getProductInfo(int productId){
        return productDao.findProductById(productId);
    }

    @Override
    public Map<String, Object> searchProduct(Long userId,String keyword, int status, int paging){
        int pageSize = 6;
        int limit = 7;
        int offset = paging * pageSize;
        List<ClientProduct> clientProducts = new ArrayList<>();
        switch (status){
            case 0:
                clientProducts= productDao.searchProductsByKeyword(userId,keyword,0,limit,offset);
                break;
            case 1:
                clientProducts= productDao.searchProductsByKeyword(userId,keyword,1,limit,offset);
                break;
        }
//        if (clientProducts.isEmpty()) {
//            throw new NoSuchElementException("No products found for the given paging");
//        }
        int productCount = clientProducts.size();
        Integer nextPaging = null;
        if (productCount > pageSize){
            nextPaging = paging + 1;
            productCount  = pageSize;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("products", clientProducts);
        if (nextPaging != null) {
            result.put("next_paging", nextPaging);
        }
        return result;
    }

    public String uploadProductImage(MultipartFile file) throws IOException {
        File convertedFile = convertMultiPartToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(getBucketName(), fileName, convertedFile));
        convertedFile.delete();
        return "https://" + s3BaseUrl + "/" + fileName;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    private String getBucketName() {
        // 從s3BaseUrl中提取bucket名稱
        return s3BaseUrl.split("\\.")[0];
    }

}
