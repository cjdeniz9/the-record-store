package org.yearup.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.*;
import org.yearup.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    private final ProfileRepository profileRepository;

    public OrderService(OrderRepository orderRepository, OrderLineItemRepository orderLineItemRepository, ShoppingCartRepository shoppingCartRepository, ProductRepository productRepository, ProfileRepository profileRepository) {
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.productRepository = productRepository;
        this.profileRepository = profileRepository;
    }

    // Creates an order from the user's shopping cart
    @Transactional
    public Order checkout(int userId) {
        Profile profile = profileRepository.findByUserId(userId);

        // Create a new order using the user's profile information
        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDate.now());
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        order.setShippingAmount(BigDecimal.ZERO);
        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);

        // Prevent checkout if the cart is empty
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Create an order line item for each product in the cart
        for(CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow();

            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setOrderId(savedOrder.getOrderId());
            lineItem.setProductId(cartItem.getProductId());
            lineItem.setSalesPrice(BigDecimal.valueOf(product.getPrice()));
            lineItem.setQuantity(cartItem.getQuantity());
            lineItem.setDiscount(BigDecimal.ZERO);

            orderLineItemRepository.save(lineItem);
        }

        // Clear the user's cart after a successful checkout
        shoppingCartRepository.deleteByUserId(userId);

        return savedOrder;
    }

}
