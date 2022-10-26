package com.moodle.notes.Repositories;

import com.moodle.notes.Models.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findNotesByGroupname(String groupname);
    Note getTopByOrderByIdDesc();
}
