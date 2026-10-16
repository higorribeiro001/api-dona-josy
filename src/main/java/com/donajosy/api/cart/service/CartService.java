package com.donajosy.api.cart.service;

import com.donajosy.api.cart.entity.Cart;
import com.donajosy.api.cart.repository.CartRepository;
import com.donajosy.api.auth.entity.User;
import com.donajosy.api.auth.repository.UserRepository;
import com.donajosy.api.product.entity.Product;
import com.donajosy.api.product.repository.ProductRepository;
import com.donajosy.api.cart.entity.StatusCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.donajosy.api.product.entity.ProductCategory;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.donajosy.api.notification.entity.UserNotification;
import com.donajosy.api.notification.repository.UserNotificationRepository;

import java.util.UUID;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserNotificationRepository notificationRepository;

    public Page<Cart> getCarts(Pageable pageable, Authentication authentication) {
        return search(null, null, null, pageable, authentication);
    }

    public Page<Cart> search(String name, ProductCategory category, StatusCart statusCart,
                             Pageable pageable, Authentication authentication) {
        boolean isAdmin = isAdmin(authentication);
        UUID userId = null;
        if (!isAdmin) {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            userId = user.getId();
            // Name/category filters are reserved for administrators.
            name = null;
            category = null;
        }
        String query = name == null || name.isBlank() ? null : name.trim();
        final UUID filteredUserId = userId;
        final String nameQuery = query;
        final ProductCategory categoryFilter = category;
        final StatusCart statusFilter = statusCart;
        Pageable oldestFirst = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "createdAt").and(Sort.by(Sort.Direction.ASC, "id")));
        Specification<Cart> specification = (root, criteriaQuery, criteriaBuilder) -> {
            Join<Cart, Product> product = root.join("product");
            var predicates = new ArrayList<Predicate>();
            if (filteredUserId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), filteredUserId));
            }
            if (nameQuery != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(product.get("name")),
                        "%" + nameQuery.toLowerCase() + "%"));
            }
            if (categoryFilter != null) {
                predicates.add(criteriaBuilder.equal(product.get("category"), categoryFilter));
            }
            if (statusFilter != null) {
                predicates.add(criteriaBuilder.equal(root.get("statusCart"), statusFilter));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
        return cartRepository.findAll(specification, oldestFirst);
    }

    public BigDecimal totalForUser(StatusCart statusCart, Authentication authentication) {
        if (isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only users can request their cart total");
        }
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return cartRepository.sumForUser(user.getId(), statusCart);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    public Page<Cart> getCartsByName(String name, Pageable pageable) { return cartRepository.findByProduct_NameContainingIgnoreCase(name, pageable); }

    public Page<Cart> getCartsByStatusCart(StatusCart statusCart, Pageable pageable, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        if (isAdmin) {
            return cartRepository.findAllByStatusCart(statusCart, pageable);
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        return cartRepository.findAllByUser_IdAndStatusCart(user.getId(), statusCart, pageable);
    }

    public Page<Cart> getCartsByNameAndStatusCart(String name, StatusCart statusCart, Pageable pageable) {
        return cartRepository.findAllByProduct_NameContainingIgnoreCaseAndStatusCart(name, statusCart, pageable);
    }

    public Cart getCartById(UUID id, Authentication authentication) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        if (!isAdmin(authentication) && !cart.getUser().getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
        }
        return cart;
    }

    public Cart create(UUID productId, StatusCart statusCart, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        if (!product.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is inactive");
        }
        Cart cart = new Cart(user, product, statusCart);
        return cartRepository.save(cart);
    }

    public Cart save(Cart cart) { return cartRepository.save(cart); }

    @Transactional
    public Cart update(UUID id, StatusCart statusCart, Authentication authentication) {
        if (statusCart == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart status is required");
        }
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Cart with id: " + id + " not found"
                        )
                );

        boolean admin = isAdmin(authentication);
        if (!admin) {
            if (!cart.getUser().getEmail().equalsIgnoreCase(authentication.getName())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
            }
            if (statusCart != StatusCart.CANCELADO) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Users can only cancel their own cart items");
            }
        }

        StatusCart previousStatus = cart.getStatusCart();
        cart.setStatusCart(statusCart);
        Cart updatedCart = cartRepository.save(cart);
        if (!statusCart.equals(previousStatus)) {
            String productName = cart.getProduct() == null ? "seu item" : cart.getProduct().getName();
            notificationRepository.save(new UserNotification(
                    cart.getUser(),
                    "O status de " + productName + " foi atualizado para " + statusCart.name() + "."
            ));
        }
        return updatedCart;
    }
}


