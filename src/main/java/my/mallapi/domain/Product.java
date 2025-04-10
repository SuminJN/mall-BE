package my.mallapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_product")
@Getter
@ToString(exclude = "imageList")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;

    private String pname;

    private int price;

    private String pdesc;

    private boolean delFlag;

    @ElementCollection // 엔티티가 아닌 값 타입, 임베디드 타입에 대한 테이블을 생성하고 1대다 관계로 다룬다.
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

    public void changeDel(boolean delFlag) {
        this.delFlag = delFlag;
    }

    public void changeName(String pname) {
        this.pname = pname;
    }

    public void changePrice(int price) {
        this.price = price;
    }

    public void changeDesc(String pdesc) {
        this.pdesc = pdesc;
    }

    public void addImage(ProductImage image) {
        image.setOrd(this.imageList.size());
        imageList.add(image);
    }

    public void addImageString(String fileName) {
        ProductImage productImage = ProductImage.builder()
                .fileName(fileName)
                .build();

        addImage(productImage);
    }

    public void clearList() {
        this.imageList.clear();
    }
}
