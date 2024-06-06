package com.teamchallenge.bookti.subscription;

import com.teamchallenge.bookti.security.AuthorizedUser;
import com.teamchallenge.bookti.user.dto.UserSubscriptionDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Subscription endpoints")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    public ResponseEntity<List<UserSubscriptionDTO>> findAllUserSubscriptionById(@RequestParam("userId") Integer userId) {
        log.info(
                "SubscriptionController::findAllUserSubscriptionById - Get /subscription/ - return list or empty list, received review to user: {}.", userId);
        return ResponseEntity.ok(subscriptionService.findAllUserSubscriptionById(userId));
    }


    @PostMapping
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    public ResponseEntity<String> save(@AuthenticationPrincipal AuthorizedUser authorizedUser,
                                       @RequestParam("userId") Integer subscriId) {
        String st = subscriptionService.save(authorizedUser.getId(), subscriId);
        log.info(
                "SubscriptionController::save - Post /subscription/ - return successfully subscription message.");
        return ResponseEntity.ok(st);
    }

    @GetMapping("/status")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    public ResponseEntity<Boolean> checkIfUserIsSubscribed(@AuthenticationPrincipal AuthorizedUser authorizedUser,
                                                           @RequestParam("userId") Integer subscriId) {
        Boolean st = subscriptionService.checkIfUserIsSubscribed(authorizedUser.getId(), subscriId);
        log.info(
                "SubscriptionController::checkIfUserIsSubscribed - Get /subscription/status - return Boolean info about subscription.");
        return ResponseEntity.ok(st);
    }

    @DeleteMapping("/{subscriptionId}")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    public ResponseEntity<String> deleteById(@PathVariable("subscriptionId") Integer subscriptionId) {
        String st = subscriptionService.deleteById(subscriptionId);
        log.info(
                "SubscriptionController::deleteById - Delete /subscription/{subscriptionId} - return successfully deleted subscription message.");
        return ResponseEntity.ok(st);
    }
}
