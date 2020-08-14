package com.company.sessionapplication.Services;

import com.company.sessionapplication.Models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.RouteMatcher.Route;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
