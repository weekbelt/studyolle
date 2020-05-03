package me.weekbelt.studyolle.event;

import lombok.RequiredArgsConstructor;
import me.weekbelt.studyolle.account.CurrentAccount;
import me.weekbelt.studyolle.domain.Account;
import me.weekbelt.studyolle.domain.Study;
import me.weekbelt.studyolle.event.form.EventForm;
import me.weekbelt.studyolle.study.StudyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;

    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path,
                               Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        model.addAttribute("study", study);
        model.addAttribute("account", account);
        model.addAttribute("eventForm", new EventForm());
        return "event/form";
    }

}
