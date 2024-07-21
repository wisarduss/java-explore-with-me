package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
