package my.mallapi.cart.repository;

import my.mallapi.cart.domain.CartItem;
import my.mallapi.cart.dto.CartItemListDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select " +
            "new my.mallapi.cart.dto.CartItemListDTO(ci.cino, ci.qty, p.pno, p.pname, p.price, pi.fileName) " +
            "from " +
            "CartItem ci inner join Cart mc on ci.cart = mc " +
            "left join Product p on ci.product = p " +
            "left join p.imageList pi " +
            "where " +
            "mc.owner.email = :email and pi.ord = 0 " +
            "order by ci.cino desc ")
    List<CartItemListDTO> getItemsOfCartDTOByEmail(@Param("email") String email);

    @Query("select ci " +
            "from CartItem ci inner join Cart c on ci.cart = c " +
            "where c.owner.email = :email and ci.product.pno = :pno")
    CartItem getItemOfPno(@Param("email") String email, @Param("pno") Long pno);

    @Query("select c.cno " +
            "from Cart c inner join CartItem ci on ci.cart = c " +
            "where ci.cino = :cino")
    Long getCartFromItem(@Param("cino") Long cino);

    @Query("select new my.mallapi.cart.dto.CartItemListDTO(ci.cino, ci.qty, p.pno, p.pname, p.price, pi.fileName) " +
            "from CartItem ci inner join Cart mc on ci.cart = mc " +
            "left join Product p on ci.product = p " +
            "left join p.imageList pi " +
            "where mc.cno = :cno and pi.ord = 0 " +
            "order by ci.cino desc")
    List<CartItemListDTO> getItemsOfCartDTOByCart(@Param("cno") Long cno);
}
