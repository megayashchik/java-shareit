package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "start_date")
	LocalDateTime start;

	@Column(name = "end_date")
	LocalDateTime end;

	@ManyToOne(fetch = FetchType.LAZY)
	@ToString.Exclude
	@JoinColumn(name = "item_id", nullable = false)
	Item item;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	Status status;

	@ManyToOne(fetch = FetchType.LAZY)
	@ToString.Exclude
	@JoinColumn(name = "booker_id", nullable = false)
	User booker;
}