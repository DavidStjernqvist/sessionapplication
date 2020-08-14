package com.company.sessionapplication.Services;

import com.company.sessionapplication.Models.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    Notice findById(int id);
}
