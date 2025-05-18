package my.mallapi.repository;

import lombok.extern.log4j.Log4j2;
import my.mallapi.product.domain.Product;
import my.mallapi.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Log4j2
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    public void testInsert() {

        for (int i = 0; i < 10; i++) {
            Product product = Product.builder()
                    .pname("상품" + i)
                    .price(100*i)
                    .pdesc("상품설명 " + i)
                    .build();

            // 2개의 이미지 파일 추가
            product.addImageString(UUID.randomUUID() +"_"+"IMAGE1.jpg");
            product.addImageString(UUID.randomUUID() +"_"+"IMAGE2.jpg");

            productRepository.save(product);

            log.info("-----------------------------");
        }
    }

    @Test
    @Transactional
    public void testRead() {
        Long pno = 1L;

        Optional<Product> result = productRepository.findById(pno);

        Product product = result.orElseThrow();

        log.info(product);
        log.info(product.getImageList());
    }

    @Test
    public void testRead2() {
        Long pno = 1L;

        Optional<Product> result = productRepository.selectOne(pno);

        Product product = result.orElseThrow();

        log.info(product);
        log.info(product.getImageList());
    }

    @Commit
    @Transactional
    @Test
    public void testDelete() {
        Long pno = 2L;

        productRepository.updateToDelete(pno, true);
    }

    @Test
    public void testUpdate() {
        Long pno = 10L;

        Product product = productRepository.selectOne(pno).orElseThrow();

        product.changeName("10번 상품");
        product.changeDesc("10번 상품 설명");
        product.changePrice(5000);

        // 첨부파일 수정
        product.clearList();
        product.addImageString(UUID.randomUUID() +"_"+"NEWIMAGE1.jpg");
        product.addImageString(UUID.randomUUID() +"_"+"NEWIMAGE2.jpg");
        product.addImageString(UUID.randomUUID()+"_"+"NEWIMAGE3.jpg");

        productRepository.save(product);
    }

    @Test
    public void testList() {
        Pageable pageable = PageRequest.of(0,  10, Sort.by("pno").descending());

        Page<Object[]> result = productRepository.selectList(pageable);

        result.getContent().forEach(arr -> log.info(Arrays.toString(arr)));
    }
}