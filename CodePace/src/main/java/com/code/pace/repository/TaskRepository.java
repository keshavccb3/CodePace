package com.code.pace.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.code.pace.model.Task;
import com.code.pace.model.User;

@Repository
public interface TaskRepository extends JpaRepository<Task,Integer>{

	List<Task> findByUserId(Integer id);

}
