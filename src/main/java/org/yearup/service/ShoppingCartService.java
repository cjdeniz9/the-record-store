package org.yearup.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.CartItem;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.repository.ShoppingCartRepository;

import java.util.List;

@Service
public class ShoppingCartService
{
    // a shopping cart is built from cart rows plus a product lookup for each row
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService)
    {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
    }

    public ShoppingCart getByUserId(int userId) {
        // load the user's cart rows, look up each product, and build the ShoppingCart
        List<CartItem> userItems = shoppingCartRepository.findByUserId(userId);

        ShoppingCart cart = new ShoppingCart();

        for (CartItem userItem : userItems) {
            Product product = productService.getById(userItem.getProductId());

            ShoppingCartItem item = new ShoppingCartItem();
            item.setProduct(product);
            item.setQuantity(userItem.getQuantity());

            cart.add(item);
        }

        return cart;
    }

    // add additional methods here
    public void addToCart(int userId, int productId) {
        CartItem existing = shoppingCartRepository.findByUserIdAndProductId(userId, productId);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
            shoppingCartRepository.save(existing);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(1);

            shoppingCartRepository.save(newItem);
        }
    }

    public CartItem updateCartItem(int userId, int productId, int quantity) {
        CartItem existingItem = shoppingCartRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not in cart");
        }

        existingItem.setQuantity(quantity);

        return shoppingCartRepository.save(existingItem);
    }

    @Transactional
    public void clearCart(int userId) {
        shoppingCartRepository.deleteByUserId(userId);
    }
}
