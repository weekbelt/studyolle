package me.weekbelt.studyolle;

import lombok.RequiredArgsConstructor;
import me.weekbelt.studyolle.account.AccountService;
import me.weekbelt.studyolle.account.SignUpForm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;

    // @WithUserDetails(value = "joohyuk", setupBefore = TestExecutionEvent.TEST_EXECUTION)가 원래 하는일
    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String nickname = withAccount.value();

        // 회원 가입
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickname);
        signUpForm.setEmail(nickname + "@hanmail.net");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);

        // Authentication 만들고 SecurityContext에 넣어주기
        UserDetails principal = accountService.loadUserByUsername(nickname);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}
