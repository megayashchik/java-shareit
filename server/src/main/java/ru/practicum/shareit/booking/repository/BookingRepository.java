package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	// Бронирования пользователя (booker)
	List<Booking> findAllByBookerId(Long bookerId);

	@Query("select b " +
			"from Booking as b " +
			"where b.booker.id = :bookerId " +
			"and CURRENT_TIMESTAMP BETWEEN b.start and b.end")
	List<Booking> findAllCurrentByBooker(Long bookerId);

	@Query("select b " +
			"from Booking as b " +
			"where b.booker.id = :bookerId " +
			"and CURRENT_TIMESTAMP > b.end")
	List<Booking> findPastByBooker(Long bookerId);

	@Query("select b " +
			"from Booking as b " +
			"where b.booker.id = :bookerId " +
			"and CURRENT_TIMESTAMP < b.start")
	List<Booking> findAllFutureByBooker(Long bookerId);

	List<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status);

	// Бронирования владельца вещи (owner)
	@Query("select b " +
			"from Booking as b " +
			"where b.item.user.id = :ownerId " +
			"order by b.start desc")
	List<Booking> findAllByOwner(Long ownerId);

	@Query("select b " +
			"from Booking as b " +
			"where b.item.user.id = :ownerId " +
			"and b.status = :status " +
			"order by b.start desc")
	List<Booking> findAllByOwnerIdAndStatus(Long ownerId, Status status);

	@Query("select b " +
			"from Booking as b " +
			"where b.item.user.id = :ownerId " +
			"and CURRENT_TIMESTAMP BETWEEN b.start and b.end")
	List<Booking> findAllCurrentByOwner(Long ownerId);

	@Query("select b " +
			"from Booking as b " +
			"where b.item.user.id = :ownerId " +
			"and CURRENT_TIMESTAMP > b.end")
	List<Booking> findAllPastBookingByOwnerId(Long ownerId);

	@Query("select b " +
			"from Booking as b " +
			"where b.item.user.id = :ownerId " +
			"and CURRENT_TIMESTAMP < b.start")
	List<Booking> findAllFutureBookingByOwnerId(Long ownerId);

	// Дополнительные методы
	Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime currentTimeStamp);

	@Query("select b.start " +
			"from Booking as b " +
			"where b.item.id = :itemId " +
			"and b.status = :status " +
			"and :currentTimeStamp < b.start")
	List<LocalDateTime> findNextBookingStartByItemId(Long itemId, Status status, LocalDateTime currentTimeStamp);

	@Query("select b.end " +
			"from Booking as b " +
			"where b.item.id = :itemId " +
			"and b.status = :status " +
			"and :currentTimeStamp > b.end")
	List<LocalDateTime> findLastBookingEndByItemId(Long itemId, Status status, LocalDateTime currentTimeStamp);

	@Query("select b " +
			"from Booking as b " +
			"where b.item.id in :itemIds " +
			"and b.status = :status " +
			"and :currentTimeStamp > b.end " +
			"order by b.end desc")
	List<Booking> findLastBookingByItemIds(List<Long> itemIds, Status status, LocalDateTime currentTimeStamp);

	@Query("select b " +
			"from Booking as b " +
			"where b.item.id in :itemIds " +
			"and b.status = :status " +
			"and :currentTimeStamp < b.start " +
			"order by b.start asc")
	List<Booking> findNextBookingByItemIds(List<Long> itemIds, Status status, LocalDateTime currentTimeStamp);
}