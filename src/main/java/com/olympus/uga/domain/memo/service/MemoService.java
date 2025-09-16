package com.olympus.uga.domain.memo.service;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.memo.domain.Memo;
import com.olympus.uga.domain.memo.domain.repo.MemoJpaRepo;
import com.olympus.uga.domain.memo.error.MemoErrorCode;
import com.olympus.uga.domain.memo.presentation.dto.req.LocationUpdateReq;
import com.olympus.uga.domain.memo.presentation.dto.req.MemoCreateReq;
import com.olympus.uga.domain.memo.presentation.dto.req.MemoUpdateReq;
import com.olympus.uga.domain.memo.presentation.dto.res.MemoInfoRes;
import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.repo.UgaJpaRepo;
import com.olympus.uga.domain.uga.error.UgaErrorCode;
import com.olympus.uga.domain.uga.service.helper.UgaContributionCalculator;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.error.UserErrorCode;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.olympus.uga.global.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoJpaRepo memoJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final UserJpaRepo userJpaRepo;
    private final UgaContributionCalculator ugaContributionCalculator;
    private final UgaJpaRepo ugaJpaRepo;
    private final FamilyJpaRepo familyJpaRepo;

    // 메모 업데이트
    @Transactional
    public Response updateMemo(MemoUpdateReq req) {

        User user = userSessionHolder.getUser();

        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        if (req == null || req.content() == null) {
            throw new CustomException(MemoErrorCode.INVALID_CONTENT);
        }

        findByWriter(user).updateContent(req.content());

        user.resetWatcher();

        return Response.ok("메모가 성공적으로 저장되었습니다.");
    }

    // 위치 갱신
    @Transactional
    public Response updateLocation(LocationUpdateReq req) {
        User user = userSessionHolder.getUser();
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        if (req == null || req.location() == null) {
            throw new CustomException(MemoErrorCode.INVALID_LOCATION);
        }

        findByWriter(user).updateLocation(req.location());

        user.resetWatcher();

        return Response.ok("위치가 성공적으로 저장되었습니다.");
    }

    // 특정 유저의 메모 조회
    // 그
    @Transactional
    public MemoInfoRes getOne(Long userId) {

        userJpaRepo.findById(userSessionHolder.getUser().getId())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND))
                .addWatcher(userId);

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
}