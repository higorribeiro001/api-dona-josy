package com.donajosy.api.cart.repository;

import com.donajosy.api.cart.entity.Cart;
import com.donajosy.api.cart.entity.StatusCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;
import java.math.BigDecimal;
import com.donajosy.api.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Page<Cart> findByProduct_NameContainingIgnoreCase(String name, Pageable pageable);

    List<Cart> findAllByUser_EmailAndStatusCart(String email, StatusCart statusCart);

    Page<Cart> findAllByUser_IdAndStatusCart(UUID id, StatusCart statusCart, Pageable pageable);

    Page<Cart> findAllByStatusCart(StatusCart statusCart, Pageable pageable);

    Page<Cart> findAllByProduct_NameContainingIgnoreCaseAndStatusCart(String name, StatusCart statusCart, Pageable pageable);

    Page<Cart> findByUser_Id(UUID userId, Pageable pageable);

    @Query("SELECT c FROM Cart c JOIN c.product p WHERE (:userId IS NULL OR c.user.id = :userId) " +
            "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:category IS NULL OR p.category = :category) " +
            "AND (:statusCart IS NULL OR c.statusCart = :statusCart)")
    Page<Cart> search(@Param("userId") UUID userId, @Param("name") String name,
                      @Param("category") ProductCategory category, @Param("statusCart") StatusCart statusCart,
                      Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.price), 0) FROM Cart c JOIN c.product p " +
            "WHERE c.user.id = :userId AND (:statusCart IS NULL OR c.statusCart = :statusCart)")
    BigDecimal sumForUser(@Param("userId") UUID userId, @Param("statusCart") StatusCart statusCart);
}

