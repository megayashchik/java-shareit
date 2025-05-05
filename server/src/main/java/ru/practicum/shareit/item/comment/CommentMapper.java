package ru.practicum.shareit.item.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
	public static CommentDto mapToCommentDto(Comment comment) {
		CommentDto dto = new CommentDto();
		dto.setId(comment.getId());
		dto.setText(comment.getText());
		dto.setItemId(comment.getItem().getId());
		dto.setAuthorName(comment.getAuthor().getName());
		dto.setCreated(comment.getCreated());

		return dto;
	}

	public static Comment mapToComment(User author, Item item, CreateCommentRequest request) {
		Comment comment = new Comment();
		comment.setText(request.getText());
		comment.setItem(item);
		comment.setAuthor(author);
		comment.setCreated(LocalDateTime.now());

		return comment;
	}
}