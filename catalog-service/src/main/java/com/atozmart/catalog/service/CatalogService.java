package com.atozmart.catalog.service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.atozmart.catalog.cache.CacheHelper;
import com.atozmart.catalog.cache.ImageCache;
import com.atozmart.catalog.configuration.CatalogConfig;
import com.atozmart.catalog.dao.CatalogDao;
import com.atozmart.catalog.dto.ImageMetadataDto;
import com.atozmart.catalog.dto.ItemDto;
import com.atozmart.catalog.dto.NewItemDto;
import com.atozmart.catalog.dto.PageDto;
import com.atozmart.catalog.dto.SearchFilters;
import com.atozmart.catalog.dto.SingleStockUpdateDto;
import com.atozmart.catalog.dto.StockUpdateDto;
import com.atozmart.catalog.dto.ViewItemsDto;
import com.atozmart.catalog.entity.Category;
import com.atozmart.catalog.entity.Item;
import com.atozmart.catalog.exception.CatalogException;
import com.atozmart.catalog.s3.S3ClientHelper;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Service
public class CatalogService {

	private final CatalogDao catalogDao;

	private final S3ClientHelper s3ClientHelper;

	private final CacheHelper cacheHelper;

	private final CatalogConfig catalogConfig;

	private static final String CACHE_PREFIX;

	static {
		CACHE_PREFIX = "catalog::";
	}

	public CatalogService(CatalogDao catalogDao, S3Client s3Client, CacheHelper cacheHelper,
			CatalogConfig catalogConfig) {
		super();
		this.catalogDao = catalogDao;
		this.s3ClientHelper = new S3ClientHelper(s3Client, catalogConfig.aws().bucketName());
		this.cacheHelper = cacheHelper;
		this.catalogConfig = catalogConfig;
	}

	public ViewItemsDto getItems() throws CatalogException {
		List<Item> items = catalogDao.getAllItems();

		if (items.isEmpty())
			throw new CatalogException("no items found", HttpStatus.NOT_FOUND);

		List<ItemDto> itemsDto = items.stream().map(item -> new ItemDto(item.getId(), item.getName(),
				item.getUnitPrice(), item.getDetails(), item.getCategory().getName())).toList();
		return new ViewItemsDto(itemsDto.size(), 1, itemsDto);

	}

	public ViewItemsDto getItems(SearchFilters searchFilters, PageDto pageDto) throws CatalogException {

		Page<Item> pageItems = catalogDao.getItems(searchFilters, pageDto);

		if (pageItems.isEmpty())
			throw new CatalogException("no items found", HttpStatus.NOT_FOUND);

		List<ItemDto> itemsDto = pageItems.getContent().stream().map(item -> new ItemDto(item.getId(), item.getName(),
				item.getUnitPrice(), item.getDetails(), item.getCategory().getName())).toList();

		return new ViewItemsDto(pageItems.getNumberOfElements(), pageItems.getTotalPages(), itemsDto);

	}

	public Set<String> getAllCategories() {
		List<Category> allCategories = catalogDao.getAllCategories();
		return allCategories.stream().map(Category::getName).collect(Collectors.toSet());
	}

	public void addNewItem(NewItemDto newItemDto) throws CatalogException {
		catalogDao.addNewItem(newItemDto);
	}

	public void updateStock(StockUpdateDto stockUpdateDto) throws CatalogException {

		if (stockUpdateDto.singleStockUpdates().isEmpty())
			throw new CatalogException("provide stock details", HttpStatus.BAD_REQUEST);

		for (SingleStockUpdateDto singleStockUpdate : stockUpdateDto.singleStockUpdates()) {
			if (singleStockUpdate.incrementBy() == null && singleStockUpdate.decrementBy() == null)
				throw new CatalogException(
						"itemId: %s, provide any one update criteria, either incrementBy or decrementBy"
								.formatted(singleStockUpdate.itemId()),
						HttpStatus.BAD_REQUEST);

			if (singleStockUpdate.incrementBy() != null && singleStockUpdate.decrementBy() != null)
				throw new CatalogException(
						"itemId: %s, provide only one update criteria, either incrementBy or decrementBy"
								.formatted(singleStockUpdate.itemId()),
						HttpStatus.BAD_REQUEST);
		}

		catalogDao.updateStock(stockUpdateDto.singleStockUpdates());

	}

	public URI uploadImage(String itemId, MultipartFile image) throws CatalogException {

		validateImage(image);

		String key = getUniqueKeyForImage(itemId);
		URL url = s3ClientHelper.uploadFile(key, image);
		catalogDao.saveImageData(new ImageMetadataDto(itemId, key, url.toString()));

		try {
			cacheHelper.cachePut(CACHE_PREFIX + key, new ImageCache(image.getBytes()));
		} catch (IOException e) {
			log.debug("Exception caching file: {}", e.getMessage());
			log.info("Evicting cache if any, key: {}", CACHE_PREFIX + key);
			cacheHelper.cacheEvict(CACHE_PREFIX + key);
		}

		return URI.create(url.toString());
	}

	public Resource getImage(String id) throws CatalogException {
		String key = catalogDao.findImageData(id).uniqueKey();
		ImageCache profilePhotoCache = cacheHelper.getCache(CACHE_PREFIX + key);
		if (profilePhotoCache != null) {
			log.info("cache hit, key: {}", CACHE_PREFIX + key);
			return new ByteArrayResource(profilePhotoCache.bytes());
		}

		log.info("cache miss, key: {}", CACHE_PREFIX + key);
		byte[] fileBytes = s3ClientHelper.getFile(key);
		cacheHelper.cachePut(CACHE_PREFIX + key, new ImageCache(fileBytes));
		return new ByteArrayResource(fileBytes);
	}

	private void validateImage(MultipartFile image) throws CatalogException {
		// file type
		List<String> allowedImagesTypes = List.of(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);
		if (!allowedImagesTypes.contains(image.getContentType())) {
			throw new CatalogException("file is not in image format", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		}

		// 1MB
		if (image.getSize() > 1 * 1024 * 1024) {
			throw new CatalogException("file is too large", HttpStatus.PAYLOAD_TOO_LARGE);
		}
	}

	private String getUniqueKeyForImage(String itemId) {
		/*
		 * ex: catalog/ITEM001-yyyyMMddHHmmssSSS
		 */
		return new StringBuilder("catalog").append("/").append(itemId).toString();
	}

	public List<ImageMetadataDto> getImageMetadata(List<String> itemIds) {
		List<ImageMetadataDto> imageData = catalogDao.findImageData(itemIds);
		return imageData.stream().map(
				s -> new ImageMetadataDto(s.itemId(), null, catalogConfig.publicBaseUrl() + "/image/" + s.itemId()))
				.toList();
	}

}
