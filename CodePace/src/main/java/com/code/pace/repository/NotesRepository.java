package com.code.pace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.code.pace.model.Notes;

@Repository
public interface NotesRepository extends JpaRepository<Notes,Integer>{

}
