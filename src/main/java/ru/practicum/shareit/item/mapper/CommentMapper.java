package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class CommentMapper {
	public static Comment mapToComment(User author, Item item, CreateCommentRequest request) {
		Comment comment = new Comment();
		comment.setItem(item);
		comment.setAuthor(author);
		comment.setText(request.getText());
		comment.setCreated(LocalDateTime.now());

		return comment;
	}

	public static CommentResponse mapToCommentResponse(Comment comment) {
		CommentResponse response = new CommentResponse();
		response.setId(comment.getId());
		response.setText(comment.getText());
		response.setAuthorName(comment.getAuthor().getName());
		response.setCreated(comment.getCreated());

		return response;
	}

	public static List<CommentResponse> mapToCommentList(List<Comment> comments) {
		return comments.stream()
				.map(CommentMapper::mapToCommentResponse)
				.toList();
	}
}