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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
        if (cookies == null) {
            isCookie = false;
        } else {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("SESSION_ID")) {
                    if (!cookie.getValue().isEmpty()) {
                        return "redirect:/admin/manage/groups";
                    }
                    isCookie = true;
                    return "redirect:/admin/manage/login";
                }
                isCookie = false;
            }
        }
        if(!isCookie){
            res.addCookie(new Cookie("SESSION_ID", ""));
        } else{
            System.out.println();
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

    @GetMapping("/admin/manage/password/{username}")
    public String update_admin_get(@PathVariable("username") String username, Model m, HttpServletRequest req, HttpServletResponse res){
        Admin currentAdmin = adminService.getAdminByUsername(getCurrentAdmin(req));
        if(currentAdmin == null){
            return "redirect:/admin/manage";
        }
        m.addAttribute("currentAdmin", currentAdmin);
        m.addAttribute("username", username);
        return "Admin/UpdatePasswordAdminPage";
    }

    @PostMapping("/admin/manage/password/{username}")
    public String update_admin_post(@PathVariable("username") String username, @RequestParam("update") String update, @RequestParam("password") String password) throws NoSuchAlgorithmException {
        adminService.update(username,password);
        return "redirect:/admin/manage/admins";
    }

    @GetMapping("/admin/manage/update/{group}")
    public String update_get(@PathVariable("group") String group, Model m, HttpServletRequest req, HttpServletResponse res){
        Admin currentAdmin = adminService.getAdminByUsername(getCurrentAdmin(req));
        if(currentAdmin == null){
            return "redirect:/admin/manage";
        }
        m.addAttribute("currentAdmin", currentAdmin);
        m.addAttribute("group", group);
        return "Admin/UpdatePasswordGroupPage";
    }

    @PostMapping("/admin/manage/update/{group}")
    public String update_post(@PathVariable("group") String group, @RequestParam("update") String update, @RequestParam("password") String password) throws NoSuchAlgorithmException {
        groupService.update(group,update,password);
        return "redirect:/admin/manage/groups";
    }

    @PostMapping("/admin/manage/login")
    public String login_post(RedirectAttributes redirectAttributes,HttpServletResponse res, @RequestParam("username") String username, @RequestParam("password") String password) throws NoSuchAlgorithmException {
        if(adminService.login(username, password)){
            log.info(String.format("Админ %s зашел.", username));
            res.addCookie(new Cookie("SESSION_ID", TokenGenerator.generateTokenByUsername(username)));
            return "redirect:/admin/manage/groups";
        } else {
            redirectAttributes.addFlashAttribute("message", "Неправильный пароль!");
            return "redirect:/admin/manage/login";
        }
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
            RedirectAttributes redirectAttributes,
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) throws NoSuchAlgorithmException {
        Admin admin = adminService.getAdminByUsername(username);
        if(password.length() >= 6 && password .length() <= 50) {
            if (admin != null) {
                return "redirect:/admin/manage/admins";
            }
            AdminRequest r = new AdminRequest();
            r.setUsername(username);
            r.setPassword(password);
            adminService.add(r);
            log.info("Добавлен новый админ: " + username);
            return "redirect:/admin/manage/admins";
        } else {
            String message = "";
            if (password.length() < 6){
                message = "Слишком мало символов!";
            } else {
                message = "Слишком много символов!";
            }
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/manage/admins/add";
        }
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
