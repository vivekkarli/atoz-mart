package com.atozmart.cart.dao;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.atozmart.cart.dto.ItemDto;
import com.atozmart.cart.entity.Cart;
import com.atozmart.cart.entity.CartCompositeId;
import com.atozmart.cart.exception.CartException;
import com.atozmart.cart.repository.CartRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@AllArgsConstructor
public class CartDao {

	private CartRepository cartRepo;

	private ModelMapper modelMapper;

	@Transactional
	public void addItemsToCart(ItemDto itemDto, String username) {

		// check if item already exists
		Optional<Cart> existingItemOpt = cartRepo.findById(new CartCompositeId(username, itemDto.getItem()));

		existingItemOpt.ifPresentOrElse(existingItem ->
		// if exists, increment quantity, update to new price
		updateItemInCart(itemDto, existingItem), () ->
		// if not exists insert new item to cart
		addNewItemToCart(itemDto, username));

	}

	public void deleteItemsFromCart(String username, String item, int quantity) throws CartException {

		if (quantity == 0) {
			cartRepo.deleteById(new CartCompositeId(username, item));
			return;
		}

		// check if item already exists
		Optional<Cart> existingItemOpt = cartRepo.findById(new CartCompositeId(username, item));

		existingItemOpt.ifPresentOrElse(existingItem -> {
			// if exists, set quantity
			log.debug("existing item: {}", existingItem);
			if (quantity >= existingItem.getQuantity()) {
				throw new CartException("quantity specified should be less than the actual quantity",
						HttpStatus.BAD_REQUEST);
			}
			existingItem.setQuantity(quantity);
			log.debug("updated item: {}", existingItem);
			cartRepo.save(existingItem);
		}, () -> {
			throw new CartException("item not present in cart", HttpStatus.NOT_FOUND);
		});

	}

	public void deleteAllByusername(String username) {
		cartRepo.deleteByUsername(username);
	}

	private void addNewItemToCart(ItemDto itemDto, String username) {
		Cart newItem = new Cart();
		newItem.setUsername(username);
		modelMapper.map(itemDto, newItem);
		log.debug("new item: {}", newItem);

		cartRepo.save(newItem);
	}

	private void updateItemInCart(ItemDto newItem, Cart existingItem) {
		log.debug("existing item: {}", existingItem);

		Cart updatedItem = new Cart();
		modelMapper.map(existingItem, updatedItem);
		updatedItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
		updatedItem.setUnitPrice(newItem.getUnitPrice());
		log.debug("updated item: {}", updatedItem);

		cartRepo.save(updatedItem);
	}

	public List<Cart> getCartDetails(String username) {
		return cartRepo.findByUsername(username);
	}

}
