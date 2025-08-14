package com.code.pace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.code.pace.model.Notes;
import com.code.pace.model.User;

@Service
public interface NotesService {

	public Notes saveNotes(Notes notes);

	public Notes findById(Integer id);

	public void deleteById(Integer id);

	public List<Notes> findByUser(User user);
	
}
