package com.code.pace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.code.pace.model.Notes;
import com.code.pace.model.User;

@Repository
public interface NotesRepository extends JpaRepository<Notes,Integer>{

	List<Notes> findByUser(User user);

}
