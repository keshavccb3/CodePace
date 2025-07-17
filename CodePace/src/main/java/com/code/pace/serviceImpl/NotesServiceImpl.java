package com.code.pace.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.code.pace.model.Notes;
import com.code.pace.repository.NotesRepository;
import com.code.pace.service.NotesService;

@Service
public class NotesServiceImpl implements NotesService{
	@Autowired
	private NotesRepository notesRepository;

	@Override
	public Notes saveNotes(Notes notes) {
		return notesRepository.save(notes);
	}

	@Override
	public Notes findById(Integer id) {
		return notesRepository.findById(id).orElse(null);
	}

	@Override
	public void deleteById(Integer id) {
		notesRepository.deleteById(id);
		
	}

}
