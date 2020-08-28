package skphone.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiImplicitParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import skphone.domain.Token;
import skphone.domain.User;
import skphone.domain.UserParam;
import skphone.service.TokenService;
import skphone.service.UserService;
import skphone.utils.reactive.CacheUtil;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping(path = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> signUp(@RequestBody @Valid UserParam userInfo) {

        return
            userService.findById(userInfo.getUsername())
                       .flatMap(__ -> Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "The userId already exists : " + userInfo.getUsername())))
                       .switchIfEmpty(userService.addUser(userInfo.getUsername(), passwordEncoder.encode(userInfo.getPassword())))
                       .cast(User.class)
                       .then()
            ;
    }

    @PostMapping(path = "/signin", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Token> signIn(@RequestBody @Valid UserParam userInfo) {

        return
            userService.findById(userInfo.getUsername())
                       .filter(user ->  passwordEncoder.matches(userInfo.getPassword(), user.getPassword()))
                       .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found user info or invalid password")))
                       .flatMap(user -> CacheUtil.elasticMono(() -> Mono.just(tokenService.createToken(user))))
            ;
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasAuthority('refresh')")
    @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer ...")
    public Mono<Token> refresh(@ApiIgnore @AuthenticationPrincipal UserDetails userInfo) {

        log.info("##### user {}", userInfo);

        return
            Mono.justOrEmpty(userInfo)
                .map(UserDetails::getUsername)
                .flatMap(userService::findById)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not found token, Please signin again.")))
                .flatMap(user -> CacheUtil.elasticMono(() -> Mono.just(tokenService.createToken(user))))
            ;
    }


}
