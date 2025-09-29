package com.olympus.uga.domain.memo.service;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.memo.domain.Memo;
import com.olympus.uga.domain.memo.domain.repo.MemoJpaRepo;
import com.olympus.uga.domain.memo.error.MemoErrorCode;
import com.olympus.uga.domain.memo.presentation.dto.request.LocationUpdateReq;
import com.olympus.uga.domain.memo.presentation.dto.request.MemoCreateReq;
import com.olympus.uga.domain.memo.presentation.dto.request.MemoUpdateReq;
import com.olympus.uga.domain.memo.presentation.dto.response.MemoInfoRes;
import com.olympus.uga.domain.mission.service.MissionProgressService;
import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.repo.UgaJpaRepo;
import com.olympus.uga.domain.uga.error.UgaErrorCode;
import com.olympus.uga.domain.uga.service.helper.UgaContributionCalculator;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.error.UserErrorCode;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.notification.service.PushNotificationService;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import com.olympus.uga.global.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.olympus.uga.global.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoJpaRepo memoJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final UserJpaRepo userJpaRepo;
    private final UgaContributionCalculator ugaContributionCalculator;
    private final UgaJpaRepo ugaJpaRepo;
    private final FamilyJpaRepo familyJpaRepo;
    private final WebSocketService webSocketService;
    private final PushNotificationService pushNotificationService;
    private final MissionProgressService missionProgressService;

    // 메모 업데이트
    public Response updateMemo(MemoUpdateReq req) {
        User user = userSessionHolder.getUser();

        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        user.updateLastActivityAt();

        if (req == null || req.content() == null) {
            throw new CustomException(MemoErrorCode.INVALID_CONTENT);
        }

        Memo memo = findByWriter(user);
        memo.updateContent(req.content());
        memoJpaRepo.save(memo);

        user.resetWatcher();
        userJpaRepo.save(user);

        // 미션 진행도 업데이트 - 메모 작성
        missionProgressService.onMemoCreated(user);

        // 웹소켓으로 메모 업데이트 알림 (가족에게)
        if (user.getFamilyCode() != null) {
            webSocketService.notifyMemoUpdate(user.getFamilyCode(), memo);

            // 가족들에게 푸시 알림 전송 (자신 제외)
            sendMemoUpdateNotification(user);
        }

        return Response.ok("메모가 성공적으로 저장되었습니다.");
    }

    // 위치 갱신
    @Transactional
    public Response updateLocation(LocationUpdateReq req) {
        User user = userSessionHolder.getUser();
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        user.updateLastActivityAt();

        if (req == null || req.location() == null) {
            throw new CustomException(MemoErrorCode.INVALID_LOCATION);
        }

        Memo memo = findByWriter(user);
        memo.updateLocation(req.location());
        memoJpaRepo.save(memo);

        user.resetWatcher();
        userJpaRepo.save(user);

        // 웹소켓으로 위치 업데이트 알림 (가족에게)
        if (user.getFamilyCode() != null) {
            webSocketService.notifyMemoUpdate(user.getFamilyCode(), memo);
        }

        return Response.ok("위치가 성공적으로 저장되었습니다.");
    }

    // 특정 유저의 메모 조회
    @Transactional
    public MemoInfoRes getOne(Long userId) {
        User user = userJpaRepo.findById(userSessionHolder.getUser().getId())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        if (!user.getWatcher().contains(userId)) {
            user.addWatcher(userId);
        }

        User targetUser = userJpaRepo.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        Memo memo = findByWriter(targetUser);

        // 만료 여부 검사
        if (memo.getContentUpdatedAt().isBefore(LocalDateTime.now().minusDays(1))) {
            memo.updateContent("메모가 아직 없습니다.");
        }

        if (memo.getLocationUpdatedAt().isBefore(LocalDateTime.now().minusDays(1))) {
            memo.updateLocation("위치 정보가 아직 없습니다.");
        }

        Family family = familyJpaRepo.findByMemberListContaining(userId)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        Uga uga = ugaJpaRepo.findById(family.getPresentUgaId())
                .orElseThrow(() -> new CustomException(UgaErrorCode.UGA_NOT_FOUND));

        double contributionRate = ugaContributionCalculator.calculateContributionRate(
                uga.getId(), userId, uga.getTotalGrowthDays()
        );

        return MemoInfoRes.from(
                memo.getId(),
                memo.getWriter(),
                contributionRate,
                memo.getContent(),
                memo.getLocation()
        );
    }

    // 체크된 멤버 반환
    public List<Long> checkedMember() {
        User user = userSessionHolder.getUser();
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
        return user.getWatcher();
    }

    private Memo findByWriter(User writer) {
        return memoJpaRepo.findByWriter(writer)
                .orElseGet(() -> memoJpaRepo.save(
                        MemoCreateReq.fromMemoCreateReq(writer)
                ));
    }

    // 메모 업데이트 시 가족들에게 푸시 알림 전송 (자신 제외)
    private void sendMemoUpdateNotification(User writer) {
        try {
            Family family = familyJpaRepo.findByMemberListContaining(writer.getId())
                    .orElse(null);

            if (family == null) {
                return;
            }

            List<User> familyMembers = userJpaRepo.findAllById(family.getMemberList());

            for (User member : familyMembers) {
                // 자신은 제외하고 FCM 토큰이 있는 경우에만 전송
                if (!member.getId().equals(writer.getId()) && member.getFcmToken() != null) {
                    pushNotificationService.sendMemoAddedNotification(
                            member.getFcmToken(),
                            writer.getUsername()
                    );
                }
            }
        } catch (Exception e) {
            log.warn("메모 업데이트 푸시 알림 전송 실패: {}", e.getMessage());
        }
    }
}