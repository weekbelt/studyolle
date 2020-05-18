package me.weekbelt.studyolle.modules.main;

import lombok.RequiredArgsConstructor;
import me.weekbelt.studyolle.modules.account.CurrentAccount;
import me.weekbelt.studyolle.modules.account.Account;
import me.weekbelt.studyolle.modules.notification.NotificationRepository;
import me.weekbelt.studyolle.modules.study.Study;
import me.weekbelt.studyolle.modules.study.StudyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
        if (account != null) {
            model.addAttribute(account);
        }
        List<Study> studyList = studyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false);
        model.addAttribute("studyList", studyList);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/search/study")  // TODO: size, page, sort를 Pageable에 바인딩
    public String searchStudy(String keyword, Model model,
                              @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC)
                                      Pageable pageable) {
        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);
        model.addAttribute("studyPage", studyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
        return "search";
    }
}
