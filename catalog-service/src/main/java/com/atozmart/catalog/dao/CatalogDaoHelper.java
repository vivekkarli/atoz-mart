package com.atozmart.catalog.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import com.atozmart.catalog.dto.PageDto;
import com.atozmart.catalog.dto.SearchFilters;
import com.atozmart.catalog.entity.Item;
import com.atozmart.catalog.repository.ItemRepository;

import jakarta.persistence.criteria.Predicate;

class CatalogDaoHelper {

	private CatalogDaoHelper() {

	}

	public static Specification<Item> withSearchCriteria(SearchFilters searchFilters) {
		return (root, query, builder) -> {

			Predicate corePredicate = builder.conjunction();
			
			if (searchFilters.name() != null) {
				corePredicate = builder.and(corePredicate,
						builder.like(builder.lower(root.get("name")), "%" + searchFilters.name().toLowerCase() + "%"));
			}

			if (searchFilters.fromPriceRange() != null && searchFilters.toPriceRange() != null) {
				corePredicate = builder.and(corePredicate, builder.between(root.get("unitPrice"),
						searchFilters.fromPriceRange(), searchFilters.toPriceRange()));

			}
			if (searchFilters.category() != null) {
				corePredicate = builder.and(corePredicate,
						builder.equal(root.get("category").get("name"), searchFilters.category()));
			}

			return corePredicate;
		};
	}

	public static Pageable getPageableObj(PageDto pageDto, ItemRepository itemRepo) {

		Sort sort = Sort.by(Direction.ASC, pageDto.sortBy());

		if (pageDto.sortDirection().equals("desc"))
			sort = Sort.by(Direction.DESC, pageDto.sortBy());

		int effectivePageNo = pageDto.pageNo();

		if (pageDto.isLastPage()) {
			long totalItems = itemRepo.count();
			effectivePageNo = (int) (totalItems / pageDto.size());
			if (totalItems % pageDto.size() == 0 && effectivePageNo > 0) {
				effectivePageNo--; // Adjust for the case when totalElements is an exact multiple of pageSize
			}
		}

		return PageRequest.of(effectivePageNo, pageDto.size()).withSort(sort);
	}

}
