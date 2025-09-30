package com.atozmart.authserver.configuration;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenericRedisSerializer<V> implements RedisSerializer<V> {

	private final ObjectMapper mapper;

	private final Class<V> valueClass;

	public GenericRedisSerializer(Class<V> value) {
		this.valueClass = value;
		this.mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.PROPERTY);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public byte[] serialize(V value) throws SerializationException {
		try {
			return mapper.writeValueAsBytes(value);
		} catch (JsonProcessingException e) {
			log.debug("cannot serialize app user: {}", e.getMessage());
		}
		return new byte[0];
	}

	@Override
	public V deserialize(byte[] bytes) throws SerializationException {
		try {
			return mapper.readValue(bytes, valueClass);
		} catch (Exception e) {
			log.debug("cannot de-serialize app user: {}", e.getMessage());
		}
		return null;
	}

}
