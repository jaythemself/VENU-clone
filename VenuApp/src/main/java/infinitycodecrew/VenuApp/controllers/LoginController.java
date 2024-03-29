package infinitycodecrew.VenuApp.controllers;

import infinitycodecrew.VenuApp.Security.UserRegistrationDetails;
import infinitycodecrew.VenuApp.UserService;
import infinitycodecrew.VenuApp.models.User;
import infinitycodecrew.VenuApp.models.data.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.swing.text.html.Option;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class LoginController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    private static final String userSessionkey = "user";

    public User getUserFromSession(HttpSession session){
        Integer userId = (Integer) session.getAttribute(userSessionkey);
        if (userId == null){
            return null;
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()){
            return null;
        } return user.get();
    }
    private static void setUserInSession(HttpSession session, Optional<User> user){
        session.setAttribute(userSessionkey, user.get().getId());
    }

    @GetMapping("/login")
    public String displayLoginForm(){
        return "login";
    }

    @PostMapping("/login")
    public String processLoginForm(@ModelAttribute @Valid UserRegistrationDetails userDetails,
                                   UserService userService,
                                   Errors errors, HttpServletRequest request,
                                   Model model){
        System.out.println(userDetails);
        Optional<User> theUser = userRepository.findByEmail(userDetails.getUsername());

        if (theUser == null){
            errors.rejectValue("username", "user.invalid", "This user does not exist!");
            model.addAttribute("title", "Log In");
            return "/login";
        }

        String password = userDetails.getPassword();
        String newUser = userRepository.findByEmail((userDetails.getPassword())).toString();


        if (!userService.isMatchingPassword(password,  theUser.get().getPassword())){
            errors.rejectValue("password", "password.invalid", "Incorrect password!");
            model.addAttribute("title", "Log In");
            return "/login";
        }

        System.out.println("lpgged in");
        setUserInSession(request.getSession(), theUser);
        return "redirect:/";
    }
}
