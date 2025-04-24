package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	// для пользователя, который осуществляет бронирование
	@Query("select b from Booking b where b.booker.id = :bookerId order by b.start desc")
	List<Booking> findAllByBooker(Long bookerId);

	@Query("select b from Booking b " +
			"where b.booker.id = :bookerId " +
			"and b.start <= :now " +
			"and b.end > :now " +
			"order by b.start desc")
	List<Booking> findCurrentByBooker(Long bookerId, LocalDateTime now);

	@Query("select b from Booking b where b.booker.id = :bookerId and b.end < :now order by b.start desc")
	List<Booking> findPastByBooker(Long bookerId, LocalDateTime now);

	@Query("select b from Booking b where b.booker.id = :bookerId and b.start > :now order by b.start desc")
	List<Booking> findFutureByBooker(Long bookerId, LocalDateTime now);

	@Query("select b from Booking b where b.booker.id = :bookerId and b.status = :status order by b.start desc")
	List<Booking> findByBookerAndStatus(Long bookerId, Status status);

	// для владельца вещи
	@Query("select b from Booking b where b.item.owner.id = :ownerId order by b.start desc")
	List<Booking> findAllByOwner(Long ownerId);

	@Query("select b from Booking b " +
			"where b.item.owner.id = :ownerId " +
			"and b.start <= :now " +
			"and b.end > :now " +
			"order by b.start desc")
	List<Booking> findCurrentByOwner(Long ownerId, LocalDateTime now);

	@Query("select b from Booking b where b.item.owner.id = :ownerId and b.end < :now order by b.start desc")
	List<Booking> findPastByOwner(Long ownerId, LocalDateTime now);

	@Query("select b from Booking b where b.item.owner.id = :ownerId and b.start > :now order by b.start desc")
	List<Booking> findFutureByOwner(Long ownerId, LocalDateTime now);

	@Query("select b from Booking b where b.item.owner.id = :ownerId and b.status = :status order by b.start desc")
	List<Booking> findByOwnerAndStatus(Long ownerId, Status status);

	// Дополнительные запросы по бронированиям
	@Query("select b from Booking b " +
			"where b.item.id in :itemIds " +
			"and b.end < :now " +
			"and b.status= :status " +
			"order by b.end desc")
	List<Booking> findLastBookingByItemIds(List<Long> itemIds, LocalDateTime now, Status status);

	@Query("select b from Booking b " +
			"where b.item.id in :itemIds " +
			"and b.start > :now " +
			"and b.status= :status " +
			"order by b.start asc")
	List<Booking> findNextBookingByItemIds(List<Long> itemIds, LocalDateTime now, Status status);

	@Query("select b from Booking b where b.item.id = :itemId and b.start > :now order by b.start asc")
	List<Booking> findFutureBookings(Long itemId, LocalDateTime now);

	@Query("select b from Booking b " +
			"where b.item.id = :itemId " +
			"and b.end < :now " +
			"and b.status = :status " +
			"order by b.end desc")
	List<Booking> findPastBookings(Long itemId, LocalDateTime now, Status status);

	@Query("select b from Booking b " +
			"where b.item.id = :itemId " +
			"and b.booker.id = :bookerId " +
			"and b.status = :status " +
			"and b.end < :end")
	List<Booking> findUserBookings(Long itemId, Long bookerId, Status status, LocalDateTime end);
}