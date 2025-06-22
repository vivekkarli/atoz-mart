package com.atozmart.wishlist.dao;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.atozmart.wishlist.entity.Wishlist;
import com.atozmart.wishlist.exception.WishlistException;
import com.atozmart.wishlist.repository.WishlistRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class WishlistDao {

	private final WishlistRepository wishlistRepo;

	public List<Wishlist> getAllByUsername(String username) {
		List<Wishlist> wishlists = wishlistRepo.findByUsername(username);

		if (wishlists.isEmpty())
			throw new WishlistException("wishlist is empty", HttpStatus.NOT_FOUND);

		return wishlists;
	}

	public void addItem(Wishlist wishlist, String username) throws WishlistException {
		wishlist.setUsername(username);
		try {
			wishlistRepo.save(wishlist);
		} catch (DataIntegrityViolationException e) {
			throw new WishlistException("item already present", HttpStatus.BAD_REQUEST);
		}
	}

	@Transactional
	public void deleteItems(String username, String itemId) {
		if (itemId == null || itemId.isBlank()) {
			// delete all by username
			wishlistRepo.deleteByUserName(username);
			return;
		}

		// delete by username and itemname
		wishlistRepo.deleteByUserNameAndItemId(username, itemId);

	}

}
