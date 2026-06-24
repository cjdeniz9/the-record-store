package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.models.*;
import org.yearup.service.ShoppingCartService;
import org.yearup.service.UserService;

import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("cart")
@CrossOrigin
@PreAuthorize("isAuthenicated()")
public class ShoppingCartController
{
    // a shopping cart controller depends on the service layer
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;

    public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService) {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
    }

    // each method in this controller requires a Principal object as a parameter
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        // get the currently logged in username
        String userName = principal.getName();
        // find database user by username
        User user = userService.getByUserName(userName);
        int userId = user.getId();

        // use the shoppingCartService to get all items in the cart and return the cart
        return shoppingCartService.getByUserId(userId);
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15  (15 is the productId to be added)
    // return the updated cart with status 201 Created
    @PostMapping("/products/{id}")
    public ResponseEntity<ShoppingCart> addToCart(Principal principal, @PathVariable int id) {
        String userName = principal.getName();
        User user = userService.getByUserName(userName);
        int userId = user.getId();

        shoppingCartService.addToCart(userId, id);

        ShoppingCart cart = shoppingCartService.getByUserId(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15  (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated; return the cart (200 OK)
    @PutMapping("/products/{id}")
    public ResponseEntity<ShoppingCart> updateCart(Principal principal, @PathVariable int id, @RequestBody ShoppingCartItem item) {
        String userName = principal.getName();
        User user = userService.getByUserName(userName);
        int userId = user.getId();

        shoppingCartService.updateCartItem(userId, id, item.getQuantity());

        ShoppingCart cart = shoppingCartService.getByUserId(userId);

        return ResponseEntity.ok(cart);
    }

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart  - return the (now empty) cart so the front end can refresh it (200 OK)
    @DeleteMapping
    public ResponseEntity<ShoppingCart> clearCart(Principal principal) {
        String userName = principal.getName();
        User user = userService.getByUserName(userName);
        int userId = user.getId();

        shoppingCartService.clearCart(userId);

        ShoppingCart cart = shoppingCartService.getByUserId(userId);

        return ResponseEntity.ok(cart);
    }
}
