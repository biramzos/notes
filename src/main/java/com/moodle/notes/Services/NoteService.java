package com.moodle.notes.Services;

import com.moodle.notes.DataAccessObject.NoteRequest;
import com.moodle.notes.Models.Note;
import com.moodle.notes.Repositories.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NoteService {
    @Autowired
    private NoteRepository repository;

    public void add(NoteRequest request, String groupname){
        repository.save(new Note(request.getTitle(), request.getContent(),groupname));
    }

    public List<Note> getNotesByGroupname(String groupname){
        return repository.findNotesByGroupname(groupname);
    }

    public Note getLastNote(){
        return repository.getTopByOrderByIdDesc();
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }
}
