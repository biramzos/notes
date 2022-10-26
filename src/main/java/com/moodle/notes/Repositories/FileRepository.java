package com.moodle.notes.Repositories;

import com.moodle.notes.Models.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findFilesByNoteId(Long noteId);
    File findFileById(Long id);
    void deleteById(Long id);
}
