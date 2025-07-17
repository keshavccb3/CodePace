package com.code.pace.service;

import org.springframework.stereotype.Service;

import com.code.pace.model.Notes;

@Service
public interface NotesService {

	public Notes saveNotes(Notes notes);

	public Notes findById(Integer id);

	public void deleteById(Integer id);
	
}
