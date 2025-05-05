package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "description", nullable = false)
	String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@ToString.Exclude
	@JoinColumn(name = "requestor_id")
	User requestor;

	@Column(name = "created")
	LocalDateTime created;
}