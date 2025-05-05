package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
	List<Item> findAllByUserId(Long ownerId);

	@Query("select i from Item i " +
			"join i.user u " +
			"where i.available = true " +
			"and (lower(i.name) like lower(concat('%', :text, '%')) " +
			"or lower(i.description) like lower(concat('%', :text, '%')))")
	List<Item> findItemsByText(String text);

	List<Item> findByRequestId(Long requestId);

	List<Item> findByRequestIdIn(List<Long> requestIds);
}