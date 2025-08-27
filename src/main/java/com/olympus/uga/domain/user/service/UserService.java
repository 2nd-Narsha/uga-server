package com.olympus.uga.domain.user.service;

import com.olympus.uga.domain.album.domain.repo.CommentJpaRepo;
import com.olympus.uga.domain.album.domain.repo.PostJpaRepo;
import com.olympus.uga.domain.answer.domain.repo.AnswerJpaRepo;
import com.olympus.uga.domain.calendar.domain.Schedule;
import com.olympus.uga.domain.calendar.domain.repo.ScheduleJpaRepo;
import com.olympus.uga.domain.calendar.domain.repo.ScheduleParticipantJpaRepo;
import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.letter.domain.repo.LetterJpaRepo;
import com.olympus.uga.domain.question.domain.repo.QuestionJpaRepo;
import com.olympus.uga.domain.uga.domain.repo.UgaContributionJpaRepo;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.presentation.dto.response.UserInfoRes;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.image.service.ImageService;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import com.olympus.uga.global.security.jwt.service.JwtTokenService;
import com.olympus.uga.global.security.jwt.util.JwtExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserSessionHolder userSessionHolder;
    private final UserJpaRepo userJpaRepo;
    private final JwtExtractor jwtExtractor;
    private final JwtTokenService jwtTokenService;
    private final ImageService imageService;
    private final FamilyJpaRepo familyJpaRepo;
    private final AnswerJpaRepo answerJpaRepo;
    private final QuestionJpaRepo questionJpaRepo;
    private final UgaContributionJpaRepo ugaContributionJpaRepo;
    private final LetterJpaRepo letterJpaRepo;
    private final ScheduleParticipantJpaRepo scheduleParticipantJpaRepo;
    private final PostJpaRepo postJpaRepo;
    private final CommentJpaRepo commentJpaRepo;

    public ResponseData<UserInfoRes> getMe() {
        User user = userSessionHolder.getUser();

        // 사용자가 속한 가족에서 리더인지 확인
        boolean isLeader = false;
        Optional<Family> family = familyJpaRepo.findByMemberListContaining(user.getId());
        if (family.isPresent()) {
            isLeader = family.get().getLeaderId().equals(user.getId());
        }

        return ResponseData.ok("사용자 정보를 성공적으로 가져왔습니다.", UserInfoRes.from(user, isLeader));
    }

    @Transactional
    public Response updateProfile(MultipartFile profileImage) {
        User user = userSessionHolder.getUser();
        user.updateProfile(imageService.uploadImage(profileImage));

        userJpaRepo.save(user);

        return Response.ok("프로필이 성공적으로 변경되었습니다.");
    }

    public Response logout(HttpServletRequest req) {
        try {
            String token = jwtExtractor.getToken(req);

            if (token != null) {
                jwtTokenService.addToBlacklist(token);
            }

            SecurityContextHolder.clearContext();

            return Response.ok("로그아웃에 성공하였습니다.");
        } catch (Exception e) { // 토큰이 유효하지 않아도 로그아웃은 성공으로 처리
            SecurityContextHolder.clearContext();
            return Response.ok("로그아웃에 성공하였습니다.");
        }
    }

    @Transactional
    public Response deleteUser() {
        User user = userSessionHolder.getUser();
        Long userId = user.getId();

        // 1. 가족 정보 가져오기
        Family userFamily = familyJpaRepo.findByMemberListContaining(userId)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        // 2. 리더인지 확인
        if (userFamily.getLeaderId().equals(userId)) {
            throw new CustomException(FamilyErrorCode.LEADER_CANNOT_LEAVE);
        }

        // 3. 가족 멤버 리스트에서 제거
        userFamily.getMemberList().remove(userId);

        // 4. 연관 데이터 삭제 순서
        // 4-1. 편지 삭제 (보낸 편지 먼저, 받은 편지 나중)
        letterJpaRepo.deleteAllBySender(user);
        letterJpaRepo.deleteAllByReceiver(user);

        // 4-2. 답변 삭제
        answerJpaRepo.deleteAllByWriter(user);

        // 4-3. 질문 삭제
        questionJpaRepo.deleteAllByWriter(user);

        // 4-4. 우가 기여도 삭제
        ugaContributionJpaRepo.deleteAllByUserId(userId);

        // 4-5. 스케줄 참여 제거
        List<Schedule> schedules = scheduleParticipantJpaRepo.findSchedulesByUserId(userId);
        for (Schedule schedule : schedules) {
            schedule.getParticipants().removeIf(p -> p.getUserId().equals(userId));
        }

        // 4-6. 앨범 댓글 삭제 (게시글보다 먼저 삭제해야 함)
        commentJpaRepo.deleteAllByWriter(user);

        // 4-7. 앨범 포스트 삭제
        postJpaRepo.deleteAllByWriter(user);

        // 5. 유저 삭제
        userJpaRepo.delete(user);

        return Response.ok("회원탈퇴에 성공하였습니다.");
    }
}
