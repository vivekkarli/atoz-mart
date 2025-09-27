package com.atozmart.cart.dao;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.atozmart.cart.dto.ItemDto;
import com.atozmart.cart.entity.Cart;
import com.atozmart.cart.exception.CartException;
import com.atozmart.cart.repository.CartRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@AllArgsConstructor
public class CartDao {

	private CartRepository cartRepo;

	private ModelMapper modelMapper;

	public void addOrUpdateItemInCart(ItemDto itemDto, String username) {

		// check if item already exists
		Optional<Cart> existingItemOpt = cartRepo.findByUsernameAndItemId(username, itemDto.itemId());

		existingItemOpt.ifPresentOrElse(existingItem ->
		// if exists, increment quantity, update to new price
		updateItemInCart(itemDto, existingItem), () ->
		// if not exists insert new item to cart
		addNewItemToCart(itemDto, username));

	}

	public void updateItemQuantity(int quantity, String itemId, String username) {

		// check if item already exists
		Cart cartItem = cartRepo.findByUsernameAndItemId(username, itemId)
				.orElseThrow(() -> new CartException("item not found", HttpStatus.NOT_FOUND));
		log.debug("existing item: {}", cartItem);

		cartItem.setQuantity(quantity);
		log.debug("updated item: {}", cartItem);

		cartRepo.save(cartItem);

	}

	public void deleteItems(String username, String itemId) {
		if (itemId == null || itemId.isBlank()) {
			cartRepo.deleteByUsername(username);
			return;
		}

		cartRepo.deleteByUsernameAndItemId(username, itemId);
	}

	private void addNewItemToCart(ItemDto itemDto, String username) {
		Cart newcart = new Cart();
		newcart.setUsername(username);
		newcart.setItemId(itemDto.itemId());
		newcart.setItemName(itemDto.itemName());
		newcart.setQuantity(itemDto.quantity());
		newcart.setUnitPrice(itemDto.unitPrice());
		log.debug("newcart: {}", newcart);

		cartRepo.save(newcart);
	}

	private void updateItemInCart(ItemDto newItem, Cart existingItem) {
		log.debug("existing item: {}", existingItem);

		Cart updatedItem = new Cart();
		modelMapper.map(existingItem, updatedItem);
		updatedItem.setQuantity(newItem.quantity());
		updatedItem.setUnitPrice(newItem.unitPrice());
		log.debug("updated item: {}", updatedItem);

		cartRepo.save(updatedItem);
	}

	public List<Cart> getCartDetails(String username) {
		return cartRepo.findByUsername(username);
	}

}
