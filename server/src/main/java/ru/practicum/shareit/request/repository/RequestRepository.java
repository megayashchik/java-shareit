package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
	List<ItemRequest> findByRequestorId(Long requestorId);

	List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long requestorId);
}