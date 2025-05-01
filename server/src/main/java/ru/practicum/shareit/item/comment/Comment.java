package ru.practicum.shareit.item.comment;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "text", nullable = false)
	String text;

	@ManyToOne(fetch = FetchType.LAZY)
	@ToString.Exclude
	@JoinColumn(name = "item_id")
	Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@ToString.Exclude
	@JoinColumn(name = "author_id")
	User author;

	@Column(name = "created")
	LocalDateTime created;
}