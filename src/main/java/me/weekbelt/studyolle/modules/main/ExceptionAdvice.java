package me.weekbelt.studyolle.modules.main;

import lombok.extern.slf4j.Slf4j;
import me.weekbelt.studyolle.modules.account.Account;
import me.weekbelt.studyolle.modules.account.CurrentAccount;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class ExceptionAdvice {

    @ExceptionHandler
    public String handleRuntimeException(@CurrentAccount Account account,
                                         HttpServletRequest req,
                                         RuntimeException e) {
        if (account != null) {
            log.info("'{}' requested '{}'", account.getNickname(), req.getRequestURI());
        } else {
            log.info("requested '{}'", req.getRequestURI());
        }
        log.error("bad request", e);
        return "error";
    }
}
