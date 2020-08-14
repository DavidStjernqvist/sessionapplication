package com.company.sessionapplication.Controllers;

import com.company.sessionapplication.Models.Comment;
import com.company.sessionapplication.Models.Notice;
import com.company.sessionapplication.Models.User;
import com.company.sessionapplication.Services.CommentRepository;
import com.company.sessionapplication.Services.NoticeRepository;
import com.company.sessionapplication.Services.UserRepository;
import com.company.sessionapplication.Utils.SessionKeeper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {

    //Sets the user during session
    private User userSession = new User();

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    NoticeRepository noticeRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model, HttpSession session){
        List<Notice> noticeList = noticeRepository.findAll();
        Collections.reverse(noticeList);
        model.addAttribute("noticeList", noticeList);
        model.addAttribute("checkSession", SessionKeeper.getSessionInstance().checkSession(session.getId()));
        model.addAttribute("userSession", userSession);
        model.addAttribute("comment", new Comment());
        System.out.println(SessionKeeper.getSessionInstance().checkSession(session.getId()));
        return "index";
    }
    @PostMapping("/comment")
    public String addComment(Notice notice){
        System.out.println("In comment");
        return "index";
    }
    @GetMapping("/login")
    public String login(@CookieValue(value = "foreverCookie", defaultValue = "") String foreverCookie,
                        HttpServletRequest request, HttpSession session, Model model){
        model.addAttribute("sessionId", session.getId());
        model.addAttribute("loginStatus", SessionKeeper.getSessionInstance().checkSession(session.getId()));
        model.addAttribute("user", new User());
        model.addAttribute("cookieFound", !foreverCookie.equals(""));
        return "login";
    }
    @GetMapping("/register")
    public String register(Model model, User user){
        model.addAttribute("user", user);
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user){
        if (userRepository.findByUsernameAndPassword(user.username, user.password) == null)
            userRepository.save(user);
        else{
            System.out.println("Couldnt register user, found matching username and password");
            return "redirect:/";
        }
        return "login";
    }
    @GetMapping("/signout")
    public String signOut(HttpSession session){
        SessionKeeper.getSessionInstance().RemoveSession(session.getId());
        if (SessionKeeper.getSessionInstance().checkSession(session.getId()) == false){
            userSession = null;
        }
        return "redirect:/";
    }
    @PostMapping(value = "/saveSession")
    public String add(@RequestParam(value = "action", required = true) String action, @ModelAttribute("user") User user,
            HttpServletRequest request, HttpSession session, Model model){

        if(userRepository.findByUsernameAndPassword(user.username, user.password) != null){
            System.out.println("Password correct: ");
            //Sets a session timer for 30 seconds
            session.setMaxInactiveInterval(30);
            //Sets the session to a local variable
            userSession = userRepository.findByUsernameAndPassword(user.username, user.password);
            SessionKeeper.getSessionInstance().addSession(session.getId());
        }
        else{
            System.out.println("Login failed");
        }
        return "redirect:/";
    }
    @GetMapping("/notice/delete/{id}")
    public String deleteNotice(@PathVariable Integer id, Model model){
        try{
            noticeRepository.deleteById(id);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return "redirect:/";
    }
    @GetMapping("/notice/add")
    public String addNotice(Model model, Notice notice){
        model.addAttribute("notice", notice);
        return "add";
    }
    @PostMapping( value = "/add")
    public String add(@ModelAttribute("notice") Notice notice, Model model){
        notice.setUser(userSession);
        noticeRepository.save(notice);
        return "redirect:/";
    }
    @GetMapping("/notice/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model){
        model.addAttribute("notice", noticeRepository.findById(id));
        return "edit";
    }
    @PostMapping(value = "/notice/edit")
    public String editNotice(@ModelAttribute("notice") Notice notice, Model model){
        try {
            Notice newNotice = noticeRepository.findById(notice.getId()).get();
            System.out.println("HELLOO");
            newNotice.setId(notice.getId());
            newNotice.setTitle(notice.getTitle());
            newNotice.setText(notice.getText());
            newNotice.setUser(notice.getUser());
            newNotice.setComments(noticeRepository.findById(notice.getId()).get().getComments());
            noticeRepository.save(newNotice);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return "redirect:/";
    }
    @GetMapping("/notice/addComment/{id}")
    public String addComment(@PathVariable("id") int noticeId, Model model, Comment comment){
        comment.setNotice(noticeRepository.findById(noticeId));
        System.out.println("IN ADDCOMMENT");
        System.out.println("Notice id: " + comment.getNotice().getId());
        comment.setUser(userSession);
        System.out.println("User id: " + userSession.id);
        model.addAttribute("comment", comment);
        return "addcomment";
    }
    @PostMapping( value = "/addComment")
    public String postComment(@ModelAttribute("comment") Comment comment, Model model){
        System.out.println("Comment text: " + comment.getText());
        System.out.println("Comment user id: " + comment.getUser().getId());
        System.out.println("Comment notice id: " + comment.getNotice().getId());
        try{
            commentRepository.save(comment);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return "redirect:/";
    }

}
