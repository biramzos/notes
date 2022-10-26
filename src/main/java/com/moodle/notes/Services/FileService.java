package com.moodle.notes.Services;

import com.moodle.notes.Models.File;
import com.moodle.notes.Repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {
    @Autowired
    private FileRepository repository;

    public void add(MultipartFile file, Long noteId) throws IOException {
        repository.save(new File(StringUtils.cleanPath(file.getOriginalFilename()), file.getBytes(),noteId));
    }

    public List<File> getFilesByNoteId(Long noteId){
        return repository.findFilesByNoteId(noteId);
    }

    public File getFileById(Long id){
        return repository.findFileById(id);
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }
}
