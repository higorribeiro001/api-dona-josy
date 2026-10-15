package com.donajosy.api.cart.repository;

import com.donajosy.api.cart.entity.Cart;
import com.donajosy.api.cart.entity.StatusCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Page<Cart> findByProduct_NameContainingIgnoreCase(String name, Pageable pageable);

    List<Cart> findAllByUser_EmailAndStatusCart(String email, StatusCart statusCart);

    Page<Cart> findAllByUser_IdAndStatusCart(UUID id, StatusCart statusCart, Pageable pageable);

    Page<Cart> findAllByStatusCart(StatusCart statusCart, Pageable pageable);

    Page<Cart> findAllByProduct_NameContainingIgnoreCaseAndStatusCart(String name, StatusCart statusCart, Pageable pageable);

    Page<Cart> findByUser_Id(UUID userId, Pageable pageable);
}

