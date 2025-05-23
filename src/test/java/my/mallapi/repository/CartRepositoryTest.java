package my.mallapi.repository;

import lombok.extern.log4j.Log4j2;
import my.mallapi.cart.domain.Cart;
import my.mallapi.cart.domain.CartItem;
import my.mallapi.cart.repository.CartItemRepository;
import my.mallapi.cart.repository.CartRepository;
import my.mallapi.member.domain.Member;
import my.mallapi.product.domain.Product;
import my.mallapi.cart.dto.CartItemListDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Transactional
    @Commit
    @Test
    public void testInsertByProduct() {

        log.info("test1-----------------------");

        // 사용자가 전송하는 정보
        String email = "user1@aaa.com";
        Long pno = 24L;
        int qty = 2;

        // 만일 기존에 사용자의 장바구니 아이템이 있었다면
        CartItem cartItem = cartItemRepository.getItemOfPno(email, pno);

        if (cartItem != null) {
            cartItem.changeQty(qty);
            cartItemRepository.save(cartItem);

            return;
        }

        // 장바구니 아이템이 없었다면 장바구니부터 확인 필요
        // 사용자가 장바구니를 만든적이 있는지 확인
        Optional<Cart> result = cartRepository.getCartOfMember(email);

        Cart cart = null;

        // 사용자의 장바구니가 존재하지 않으면 장바구니 생성
        if (result.isEmpty()) {
            log.info("MemberCart is not exist!!");

            Member member = Member.builder().email(email).build();

            Cart tempCart = Cart.builder().owner(member).build();

            cart = cartRepository.save(tempCart);
        } else {
            cart = result.get();
        }

        log.info(cart);

        if (cartItem == null) {
            Product product = Product.builder().pno(pno).build();
            cartItem = CartItem.builder().product(product).cart(cart).qty(qty).build();
        }

        // 상품 아이템 저장
        cartItemRepository.save(cartItem);
    }

    @Test
    @Commit
    public void testUpdateByCino() {

        Long cino = 1L;

        int qty = 4;

        Optional<CartItem> result = cartItemRepository.findById(cino);

        CartItem cartItem = result.orElseThrow();

        cartItem.changeQty(qty);

        cartItemRepository.save(cartItem);
    }

    @Test
    public void testListOfMemeber() {
        String email = "user1@aaa.com";

        List<CartItemListDTO> cartItemList = cartItemRepository.getItemsOfCartDTOByEmail(email);

        for(CartItemListDTO dto : cartItemList) {
            log.info(dto);
        }
    }
}