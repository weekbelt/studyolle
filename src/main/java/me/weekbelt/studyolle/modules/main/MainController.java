package me.weekbelt.studyolle.modules.main;

import lombok.RequiredArgsConstructor;
import me.weekbelt.studyolle.modules.account.CurrentAccount;
import me.weekbelt.studyolle.modules.account.Account;
import me.weekbelt.studyolle.modules.notification.NotificationRepository;
import me.weekbelt.studyolle.modules.study.Study;
import me.weekbelt.studyolle.modules.study.StudyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final StudyRepository studyRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if(account != null){
            model.addAttribute(account);
        }

        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/search/study")
    public String searchStudy(String keyword, Model model) {
        List<Study> studyList = studyRepository.findByKeyword(keyword);
        model.addAttribute("studyList", studyList);
        model.addAttribute("keyword", keyword);
        return "search";
    }
}
