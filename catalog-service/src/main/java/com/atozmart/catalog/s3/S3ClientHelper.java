package com.atozmart.catalog.s3;

import java.net.URL;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.atozmart.catalog.exception.CatalogException;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
public class S3ClientHelper {

	private final S3Client s3Client;

	private final String bucket;

	public S3ClientHelper(S3Client s3Client, String bucket) {
		this.s3Client = s3Client;
		this.bucket = bucket;
	}

	public URL uploadFile(String key, MultipartFile file) throws CatalogException {
		try {
			if (!doesBucketExists(bucket)) {
				createBucket(bucket);
			}

			log.info("upload file to s3 bucket: {}", bucket);
			PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket)
					.contentType(file.getContentType()).key(key).build();

			RequestBody requestBody = RequestBody.fromBytes(file.getBytes());

			s3Client.putObject(putObjectRequest, requestBody);

			return getURL(key);

		} catch (Exception e) {
			log.debug("Exception uploading file to s3, {}", e.getMessage());
			throw new CatalogException("unable to upload file at the moment", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void createBucket(String bucket) throws CatalogException {
		log.info("creating bucket: {}", bucket);
		try {
			CreateBucketRequest request = CreateBucketRequest.builder().bucket(bucket).build();
			s3Client.createBucket(request);
		} catch (BucketAlreadyExistsException e) {
			log.debug("Exception creating bucket: {}, {}", bucket, e.getMessage());
			throw new CatalogException("bucket already exists", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public boolean doesBucketExists(String bucket) {
		try {
			HeadBucketRequest request = HeadBucketRequest.builder().bucket(bucket).build();
			s3Client.headBucket(request);
			return true;
		} catch (NoSuchBucketException e) {
			log.debug("Exception checking bucket: {}", e.getMessage());
		}
		return false;
	}

	public URL getURL(String key) {
		GetUrlRequest getUrlRequest = GetUrlRequest.builder().bucket(bucket).key(key).build();
		return s3Client.utilities().getUrl(getUrlRequest);
	}

	public byte[] getFile(String key) throws CatalogException {
		try {
			log.debug("getting file from s3, key {}", key);
			GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(key).build();
			ResponseBytes<GetObjectResponse> object = s3Client.getObjectAsBytes(getObjectRequest);
			return object.asByteArray();
		} catch (Exception e) {
			log.debug("exception getting file from s3: {}", e.getMessage());
			throw new CatalogException("unable to get file at the moment", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
