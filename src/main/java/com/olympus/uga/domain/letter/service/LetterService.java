package com.olympus.uga.domain.letter.service;

import com.olympus.uga.domain.letter.domain.Letter;
import com.olympus.uga.domain.letter.domain.repo.LetterJpaRepo;
import com.olympus.uga.domain.letter.error.LetterErrorCode;
import com.olympus.uga.domain.letter.presentation.dto.request.LetterReq;
import com.olympus.uga.domain.letter.presentation.dto.response.LetterListRes;
import com.olympus.uga.domain.letter.presentation.dto.response.LetterRes;
import com.olympus.uga.domain.mission.service.MissionProgressService;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.error.UserErrorCode;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.notification.service.PushNotificationService;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import com.olympus.uga.global.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LetterService {
    private final LetterJpaRepo letterJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final UserJpaRepo userJpaRepo;
    private final PushNotificationService pushNotificationService;
    private final WebSocketService webSocketService;
    private final MissionProgressService missionProgressService;

    @Transactional
    public Response writeLetter(LetterReq req) {
        User sender = userSessionHolder.getUser();

        User receiver = userJpaRepo.findById(req.receiverId())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        receiver.updateMailBox(false);
        sender.usePoint(req.point());

        // 활동 시간 업데이트
        receiver.updateLastActivityAt();
        sender.updateLastActivityAt();

        Letter savedLetter = letterJpaRepo.save(LetterReq.fromLetterReq(sender, receiver, req));
        userJpaRepo.save(receiver);
        userJpaRepo.save(sender);

        // 미션 진행도 업데이트 - 편지 발송
        missionProgressService.onLetterSent(sender);

        // 편지 도착 푸시 알림 (받는 사람에게만)
        if (receiver.getFcmToken() != null) {
            pushNotificationService.sendLetterNotification(
                    receiver.getFcmToken(),
                    sender.getUsername()
            );
        }

        // 웹소켓으로 편지 도착 알림 (받는 사람에게만)
        LetterRes letterRes = LetterRes.from(savedLetter);
        webSocketService.notifyLetterReceived(receiver.getId(), letterRes);

        // 발신자 포인트 변경 웹소켓 알림
        webSocketService.notifyPointUpdate(sender.getId(), sender.getPoint(), "LETTER_SEND");

        // 수신자 포인트 변경 웹소켓 알림
        webSocketService.notifyPointUpdate(receiver.getId(), receiver.getPoint(), "LETTER_RECEIVE");

        return Response.created(receiver.getUsername() + "에게 편지를 보냈습니다.");
    }

    // 편지함
    @Transactional(readOnly = true)
    public List<LetterListRes> getInbox() {
        User user = userSessionHolder.getUser();

        List<Letter> receivedLetters = letterJpaRepo.findByReceiver(user);

        user.updateMailBox(true);

        return receivedLetters.stream()
                .map(LetterListRes::from)
                .toList();
    }

    @Transactional
    public LetterRes getLetter(Long letterId) {
        User user = userSessionHolder.getUser();

        Letter letter = letterJpaRepo.findByIdAndReceiver(letterId, user)
                .orElseThrow(() -> new CustomException(LetterErrorCode.LETTER_NOT_FOUND));

        // 편지를 읽지 않은 상태라면 읽음 처리
        if (!letter.getIsRead()) {
            letter.markAsRead();
            letterJpaRepo.save(letter); // 변경사항 저장
            user.earnPoint(letter.getPoint());
        }

        return LetterRes.from(letter);
    }

    public Boolean isCheckedLetterBox() {
        return userSessionHolder.getUser().getIsCheckedMailbox();
    }
}
