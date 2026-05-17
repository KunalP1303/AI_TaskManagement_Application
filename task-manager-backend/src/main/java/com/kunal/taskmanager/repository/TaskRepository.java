package com.kunal.taskmanager.repository;

import com.kunal.taskmanager.entity.Task;
import com.kunal.taskmanager.entity.User;
import com.kunal.taskmanager.enums.Priority;
import com.kunal.taskmanager.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByStatusAndPriority(Status status, Priority priority, Pageable pageable);

    Page<Task> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Task> findByUser(User user, Pageable pageable);

    Page<Task> findByUserAndStatusAndPriority(User user, Status status, Priority priority, Pageable pageable);

    Page<Task> findByUserAndTitleContainingIgnoreCase(User user, String keyword, Pageable pageable);
}