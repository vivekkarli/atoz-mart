package com.atozmart.profile.service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.atozmart.profile.cache.RedisCacheHelper;
import com.atozmart.profile.configuration.ProfileConfig;
import com.atozmart.profile.dao.ProfileDao;
import com.atozmart.profile.dto.ProfileDetailsDto;
import com.atozmart.profile.dto.ProfilePhotoCache;
import com.atozmart.profile.dto.ProfilePhotoMetadataDto;
import com.atozmart.profile.entity.UserProfile;
import com.atozmart.profile.exception.ProfileException;
import com.atozmart.profile.s3.S3ClientHelper;
import com.atozmart.profile.util.ProfileMapper;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Service
public class ProfileService {

	private final ProfileDao profileDao;

	private final S3ClientHelper s3ClientHelper;

	private final RedisCacheHelper redisCacheHelper;

	private static final String CACHE_PREFIX;

	static {
		CACHE_PREFIX = "profile::";
	}

	public ProfileService(ProfileDao profileDao, S3Client s3Client, ProfileConfig profileConfig,
			RedisCacheHelper redisCacheHelper) {
		this.profileDao = profileDao;
		this.s3ClientHelper = new S3ClientHelper(s3Client, profileConfig.aws().bucketName());
		this.redisCacheHelper = redisCacheHelper;
	}

	public ProfileDetailsDto getProfileDetails(String username) {

		UserProfile userProfile = profileDao.getProfileDetails(username);
		log.debug("userProfile: {}", userProfile);

		return ProfileMapper.mapToProfileDetails(userProfile);

	}

	public void addNewProfile(String username, ProfileDetailsDto profileDetailsDto) {

		if (profileDao.doesProfileExists(username))
			throw new ProfileException("profile already exists with username: %s".formatted(username),
					HttpStatus.NOT_FOUND);

		UserProfile userProfile = ProfileMapper.mapToUserProfile(username, profileDetailsDto);

		profileDao.addOrUpdateProfile(userProfile);

	}

	public void editProfileDetails(String username, ProfileDetailsDto profileDetailsDto) throws ProfileException {

		if (!profileDao.doesProfileExists(username))
			throw new ProfileException("profile doesn't exists with username: %s".formatted(username),
					HttpStatus.NOT_FOUND);

		UserProfile userProfile = ProfileMapper.mapToUserProfile(username, profileDetailsDto);
		profileDao.addOrUpdateProfile(userProfile);

	}

	public void deleteAddress(String username, String addressType) {

		if (!profileDao.doesProfileExists(username))
			throw new ProfileException("profile doesn't exists with username: %s".formatted(username),
					HttpStatus.NOT_FOUND);

		profileDao.deleteAddress(username, addressType);
	}

	public void changeDefaultTo(String username, String addressType) {
		profileDao.changeDefaultTo(username, addressType);
	}

	public URI uploadProfilePicture(String username, MultipartFile profilePicture) {

		validateProfilePicture(profilePicture);

		String key = getUniqueKeyForProfilePhoto(username);
		URL url = s3ClientHelper.uploadFile(key, profilePicture);
		profileDao.saveProfilePhotoMetadata(new ProfilePhotoMetadataDto(username, key, url.toString()));

		try {
			redisCacheHelper.cachePut(CACHE_PREFIX + key, new ProfilePhotoCache(profilePicture.getBytes()));
		} catch (IOException e) {
			log.debug("Exception caching file: {}", e.getMessage());
			log.info("Evicting cache if any, key: {}", CACHE_PREFIX + key);
			redisCacheHelper.cacheEvict(CACHE_PREFIX + key);
		}

		return URI.create("/profile/profile-photo/" + username);
	}

	private void validateProfilePicture(MultipartFile profilePicture) {
		// file type
		List<String> allowedImagesTypes = List.of(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);
		if (!allowedImagesTypes.contains(profilePicture.getContentType())) {
			throw new ProfileException("file is not in image format", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		}

		// 1MB
		if (profilePicture.getSize() > 1 * 1024 * 1024) {
			throw new ProfileException("file is too large", HttpStatus.PAYLOAD_TOO_LARGE);
		}
	}

	private String getUniqueKeyForProfilePhoto(String username) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		String todayDateTime = LocalDateTime.now().format(dateTimeFormatter);
		String fileName = "profile-photo-" + todayDateTime;
		/*
		 * <username>/profile-photo-yyyyMMddHHmmssSSS
		 */
		return new StringBuilder(username).append("/").append(fileName).toString();
	}

	public Resource getProfilePicture(String username) {
		String key = profileDao.getProfilePhotoMetadata(username).uniqueKey();

		ProfilePhotoCache profilePhotoCache = redisCacheHelper.getCache(CACHE_PREFIX + key);
		if (profilePhotoCache != null) {
			log.info("cache hit, key: {}", CACHE_PREFIX + key);
			return new ByteArrayResource(profilePhotoCache.bytes());
		}
		log.info("cache miss, key: {}", CACHE_PREFIX + key);

		byte[] fileBytes = s3ClientHelper.getFile(key);

		redisCacheHelper.cachePut(CACHE_PREFIX + key, new ProfilePhotoCache(fileBytes));
		return new ByteArrayResource(fileBytes);
	}

}
