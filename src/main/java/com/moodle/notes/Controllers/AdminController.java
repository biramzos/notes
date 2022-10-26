package com.moodle.notes.Controllers;

import com.moodle.notes.DataAccessObject.AdminRequest;
import com.moodle.notes.DataAccessObject.CreateGroupRequest;
import com.moodle.notes.Generators.TokenGenerator;
import com.moodle.notes.Models.Admin;
import com.moodle.notes.Models.Group;
import com.moodle.notes.Services.AdminService;
import com.moodle.notes.Services.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/admin/manage/")
    public String home(HttpServletResponse res, HttpServletRequest req){
        Boolean isCookie = false;
        Cookie[] cookies = req.getCookies();
        for(Cookie cookie:cookies){
            if(cookie.getName().equals("SESSION_ID")){
                if(!cookie.getValue().isEmpty()) {
                    return "redirect:/admin/manage/groups";
                }
                isCookie = true;
                return "redirect:/admin/manage/login";
            }
            isCookie = false;
        }
        if(!isCookie){
            res.addCookie(new Cookie("SESSION_ID", ""));
        }
        return "redirect:/admin/manage/login";
    }

    @GetMapping("/admin/manage/login")
    public String login_get() throws NoSuchAlgorithmException {
        if(adminService.getAdmins().size() == 0){
            AdminRequest r = new AdminRequest();
            r.setUsername("admin");
            r.setPassword("admin");
            adminService.add(r);
        }
        return "Admin/LoginAdminPage";
    }

    @PostMapping("/admin/manage/login")
    public String login_post(HttpServletResponse res, @RequestParam("username") String username, @RequestParam("password") String password) throws NoSuchAlgorithmException {
        AdminRequest r = new AdminRequest();
        r.setUsername(username);
        r.setPassword(password);
        adminService.add(r);
        if(adminService.login(r).get("message").equals("Success")){
            log.info(String.format("Админ %s зашел.", username));
            res.addCookie(new Cookie("SESSION_ID", TokenGenerator.generateTokenByUsername(username)));
            return "redirect:/admin/manage/groups";
        }
        return "redirect:/admin/manage/login";
    }

    @GetMapping("/admin/manage/groups")
    public String groups(HttpServletRequest req, Model m, @Param("search") String search){
        Admin currentAdmin = adminService.getAdminByUsername(getCurrentAdmin(req));
        if(currentAdmin == null){
            return "redirect:/admin/manage";
        }
        m.addAttribute("currentAdmin", currentAdmin);
        if(search == null || search.isEmpty()){
            m.addAttribute("groups", groupService.getGroups());
        }
        else{
            m.addAttribute("groups", groupService.findGroups(search));
        }
        return "Admin/GroupsPage";
    }

    @GetMapping("/admin/manage/groups/add")
    public String addGroup_get(HttpServletRequest req){
        Admin currentAdmin = adminService.getAdminByUsername(getCurrentAdmin(req));
        if(currentAdmin == null){
            return "redirect:/admin/manage";
        }
        return "Admin/AddGroupPage";
    }

    @GetMapping("/admin/manage/groups/delete")
    public String deleteGroup(@Param("id") Long id){
        groupService.delete(id);
        log.info("Удален взвод.");
        return "redirect:/admin/manage/groups";
    }

    @PostMapping("/admin/manage/groups/add")
    public String addGroup_post(
            @RequestParam("groupname") String groupname,
            @RequestParam("tutor") String tutor,
            @RequestParam("student") String student,
            @RequestParam("teacher") String teacher
    ) throws NoSuchAlgorithmException {
        Group group = groupService.getGroupByGroupname(groupname);
        if(group != null){
            return "redirect:/admin/manage/groups";
        }
        CreateGroupRequest r = new CreateGroupRequest();
        r.setStudents(student);
        r.setTeachers(teacher);
        r.setGroupname(groupname);
        r.setTutor(tutor);
        groupService.register(r);
        log.info("Создан новый взвод: " + groupname + ".");
        return "redirect:/admin/manage/groups";
    }

    @GetMapping("/admin/manage/logout")
    public String logout(HttpServletResponse res){
        Cookie cookie = new Cookie("SESSION_ID","");
        res.addCookie(cookie);
        return "redirect:/admin/manage/login";
    }

    @GetMapping("/admin/manage/admins")
    public String admins(HttpServletRequest req, Model m, @Param("search") String search){
        Admin currentAdmin = adminService.getAdminByUsername(getCurrentAdmin(req));
        if(currentAdmin == null){
            return "redirect:/admin/manage";
        }
        m.addAttribute("currentAdmin", currentAdmin);
        if(search == null){
            m.addAttribute("admins", adminService.getAdmins());
        }
        else{
            m.addAttribute("admins", adminService.findAdmins(search));
        }
        return "Admin/AdminsPage";
    }

    @GetMapping("/admin/manage/admins/add")
    public String addAdmin_get(HttpServletRequest req){
        Admin currentAdmin = adminService.getAdminByUsername(getCurrentAdmin(req));
        if(currentAdmin == null){
            return "redirect:/admin/manage";
        }
        return "Admin/AddAdminPage";
    }

    @GetMapping("/admin/manage/admins/delete")
    public String deleteAdmin(@Param("username") String username){
        adminService.deleteByUsername(username);
        log.info("Удален админ: " + username);
        return "redirect:/admin/manage/admins";
    }

    @PostMapping("/admin/manage/admins/add")
    public String addAdmin_post(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) throws NoSuchAlgorithmException {
        Admin admin = adminService.getAdminByUsername(username);
        if(admin != null){
            return "redirect:/admin/manage/admins";
        }
        AdminRequest r = new AdminRequest();
        r.setUsername(username);
        r.setPassword(password);
        adminService.add(r);
        log.info("Добавлен новый админ: " + username);
        return "redirect:/admin/manage/admins";
    }

    @Nullable
    public String getCurrentAdmin(HttpServletRequest req){
        String group = "";
        Cookie[] cookies = req.getCookies();
        for(Cookie cookie:cookies){
            if(cookie.getName().equals("SESSION_ID")){
                if(!cookie.getValue().isEmpty()) {
                    group = TokenGenerator.generateUsernameByToken(cookie.getValue());
                }
            }
        }
        return group;
    }

}
