package com.moodle.notes.Controllers;

import com.moodle.notes.DataAccessObject.GroupRequest;
import com.moodle.notes.DataAccessObject.NoteRequest;
import com.moodle.notes.Generators.TokenGenerator;
import com.moodle.notes.Models.File;
import com.moodle.notes.Models.Group;
import com.moodle.notes.Models.Role;
import com.moodle.notes.Services.FileService;
import com.moodle.notes.Services.GroupService;
import com.moodle.notes.Services.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
public class NoteController {
    @Autowired
    private GroupService groupService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private FileService fileService;
    @GetMapping("/")
    public String home(HttpServletResponse res, HttpServletRequest req){
        Boolean isCookie = false;
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            isCookie = false;
        }
        else {
            for(Cookie cookie:cookies){
                if(cookie.getName().equals("SESSION")){
                    if(!cookie.getValue().isEmpty()) {
                        return "redirect:/notes";
                    }
                    isCookie = true;
                    return "redirect:/login";
                }
                isCookie = false;
                return "redirect:/login";
            }
        }
        if(!isCookie){
            Cookie cookie = new Cookie("SESSION","");
            res.addCookie(cookie);
        } else {
            System.out.println();
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login_get(Model m, HttpServletResponse res, HttpServletRequest req){
        String groupname = getCurrentGroup(req).get("groupname");
        if(groupname.isEmpty()) {
            List<String> groupnames = groupService.getGroupnames();
            m.addAttribute("groupnames", groupnames);
            return "LoginPage";
        }
        return "redirect:/notes";
    }

    @PostMapping("/login")
    public String login_post(HttpServletResponse res, HttpServletRequest req, Model m,@RequestParam("role") String role, @RequestParam("groupname") String groupname, @RequestParam("password") String password, RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException {
        GroupRequest r = new GroupRequest();
        r.setGroupname(groupname);
        r.setPassword(password);
        r.setRole(role);
        HashMap<String, String> login_res = groupService.login(r);
        if(login_res.get("groupname").length() > 2){
            Cookie cookie = new Cookie("SESSION", TokenGenerator.generateTokenByUsername(login_res.get("groupname") + " " + login_res.get("role")));
            res.addCookie(cookie);
            if(login_res.get("role").equals(Role.TEACHERS.name()))
                log.info("?????????????? ???????????? " + groupname + " ??????????.");
            else
                log.info("?????????? " + groupname + " ??????????.");
            return "redirect:/notes";
        }
        else{
            redirectAttributes.addFlashAttribute("message","???????????????????????? ????????????!");
            return "redirect:/login";
        }
    }

    @GetMapping("/notes")
    public String notes_get(Model m, HttpServletRequest req){
        Group currentGroup = groupService.getGroupByGroupname(getCurrentGroup(req).get("groupname"));
        String role = getCurrentGroup(req).get("role");
        if(currentGroup == null){
            return "redirect:/login";
        }
        m.addAttribute("notes", noteService.getNotesByGroupname(currentGroup.getGroupname()));
        m.addAttribute("group",currentGroup);
        m.addAttribute("role",role);
        m.addAttribute("fileService", fileService);
        m.addAttribute("size", noteService.getNotesByGroupname(currentGroup.getGroupname()).size());
        return "NotesPage";
    }

    @GetMapping("/add")
    public String add_note_get(Model m, HttpServletRequest req){
        Group currentGroup = groupService.getGroupByGroupname(getCurrentGroup(req).get("groupname"));
        if(currentGroup == null){
            return "redirect:/login";
        }
        m.addAttribute("group", currentGroup);
        return "AddNotePage";
    }

    @PostMapping("/add")
    public String add_note_post(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("files") MultipartFile[] files,
            HttpServletRequest req
    ) throws IOException {
        Group group = groupService.getGroupByGroupname(getCurrentGroup(req).get("groupname"));
        NoteRequest r = new NoteRequest();
        r.setTitle(title); r.setContent(content);
        noteService.add(r, group.getGroupname());
        for(MultipartFile file:files){
            fileService.add(file, noteService.getLastNote().getId());
        }
        log.info("?????????????????? ?????????? ?????????????? ?????? ???????????? " + group.getGroupname());
        return "redirect:/notes";
    }

    @GetMapping(value = "/file/{id}", produces = "application/octet-stream")
    public void downloadFile(@PathVariable("id") Long id , HttpServletResponse response) throws Exception {
        File file = fileService.getFileById(id);
        if(file == null) {
            log.error("Could not Found!");
        }
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + file.getName();
        response.setHeader(headerKey, headerValue);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(file.getContent());
        outputStream.close();
    }

    @GetMapping("/delete")
    public String delete_note(@Param("id") Long id, HttpServletRequest req){
        List<File> files = fileService.getFilesByNoteId(id);
        for(File file:files){
            fileService.deleteById(file.getId());
        }
        noteService.deleteById(id);
        log.info("?????????????? ?????????????? ???????????? " + getCurrentGroup(req).get("groupname"));
        return "redirect:/notes";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse res, HttpServletRequest req){
        log.info("?????????? " + getCurrentGroup(req).get("groupname") + " ??????????.");
        Cookie cookie = new Cookie("SESSION","");
        res.addCookie(cookie);
        return "redirect:/login";
    }

    @Nullable
    public HashMap<String, String> getCurrentGroup(HttpServletRequest req){
        String group = "";
        Cookie[] cookies = req.getCookies();
        for(Cookie cookie:cookies){
            if(cookie.getName().equals("SESSION")){
                if(!cookie.getValue().isEmpty()) {
                    group = TokenGenerator.generateUsernameByToken(cookie.getValue());
                }
            }
        }
        if(!group.isEmpty()) {
            String groupname = group.substring(0, group.indexOf(" "));
            String role = group.substring(group.indexOf(" ") + 1, group.length());
            return new HashMap<>(){{
                put("groupname", groupname);
                put("role", role);
            }};
        }
        return new HashMap<>(){{
           put("groupname","");
           put("role", "");
        }};
    }
}
