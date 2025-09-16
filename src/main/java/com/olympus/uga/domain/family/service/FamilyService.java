package com.olympus.uga.domain.family.service;

import com.olympus.uga.domain.album.domain.repo.CommentJpaRepo;
import com.olympus.uga.domain.album.domain.repo.PostImageJpaRepo;
import com.olympus.uga.domain.album.domain.repo.PostJpaRepo;
import com.olympus.uga.domain.answer.domain.repo.AnswerJpaRepo;
import com.olympus.uga.domain.calendar.domain.repo.DDayJpaRepo;
import com.olympus.uga.domain.calendar.domain.repo.ScheduleJpaRepo;
import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.family.presentation.dto.request.FamilyCreateReq;
import com.olympus.uga.domain.family.presentation.dto.request.LeaderChangeReq;
import com.olympus.uga.domain.family.presentation.dto.response.FamilyInfoRes;
import com.olympus.uga.domain.family.util.CodeGenerator;
import com.olympus.uga.domain.letter.domain.repo.LetterJpaRepo;
import com.olympus.uga.domain.memo.domain.repo.MemoJpaRepo;
import com.olympus.uga.domain.question.domain.repo.QuestionJpaRepo;
import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.UgaAsset;
import com.olympus.uga.domain.uga.domain.repo.UgaAssetJpaRepo;
import com.olympus.uga.domain.uga.domain.repo.UgaContributionJpaRepo;
import com.olympus.uga.domain.uga.domain.repo.UgaJpaRepo;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.error.UserErrorCode;
import com.olympus.uga.domain.user.util.InterestConverter;
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyJpaRepo familyJpaRepo;
    private final CodeGenerator codeGenerator;
    private final UserJpaRepo userJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final UgaAssetJpaRepo ugaAssetJpaRepo;
    private final UgaJpaRepo ugaJpaRepo;
    private final UgaContributionJpaRepo ugaContributionJpaRepo;
    private final PostJpaRepo postJpaRepo;
    private final DDayJpaRepo dDayJpaRepo;
    private final ScheduleJpaRepo scheduleJpaRepo;
    private final PostImageJpaRepo postImageJpaRepo;
    private final CommentJpaRepo commentJpaRepo;
    private final LetterJpaRepo letterJpaRepo;
    private final QuestionJpaRepo questionJpaRepo;
    private final AnswerJpaRepo answerJpaRepo;
    private final MemoJpaRepo memoJpaRepo;

    @Transactional
    public ResponseData<String> createFamily(FamilyCreateReq req) {
        User user = userSessionHolder.getUser();
        String code = codeGenerator.generateCode();

        while (familyJpaRepo.findById(code).isPresent()) {
            code = codeGenerator.generateCode();
        }

        familyJpaRepo.save(FamilyCreateReq.fromFamilyCreateReq(code, req, user.getId(), req.getFileUrl()));

        // 가족 자산 초기화 (우가 꾸미기 아이템)
        UgaAsset ugaAsset = UgaAsset.createDefault(code);
        ugaAssetJpaRepo.save(ugaAsset);

        return ResponseData.created("가족 생성에 성공했습니다.", code);
    }

    @Transactional
    public Response joinFamily(String familyCode) {
        Family family = familyJpaRepo.findById(familyCode)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));
        User user = userSessionHolder.getUser();

        if (family.getMemberList().contains(user.getId())) {
            throw new CustomException(FamilyErrorCode.ALREADY_MEMBER);
        }

        family.getMemberList().add(user.getId());

        return Response.ok("가족 " + family.getFamilyName() + "에 가입이 되었습니다.");
    }

    public ResponseData<FamilyInfoRes> getFamily() {
        User user = userSessionHolder.getUser();

        Family family = familyJpaRepo.findByMemberListContaining(user.getId())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        // 멤버 정보 조회 및 변환
        List<FamilyInfoRes.FamilyMemberInfo> memberInfos = family.getMemberList().stream()
                .map(memberId -> {
                    User memberUser = userJpaRepo.findById(memberId)
                            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

                    // 문자열을 리스트로 변환
                    List<String> interestList = InterestConverter.stringToList(memberUser.getInterests());

                    return new FamilyInfoRes.FamilyMemberInfo(
                            memberUser.getId(),
                            memberUser.getUsername(),
                            memberUser.getCharacter().getImageUrl(),
                            memberUser.getBirth(),
                            interestList,
                            memberUser.getCharacter(),
                            family.getLeaderId().equals(memberUser.getId()) // 리더 여부
                    );
                })
                .collect(Collectors.toList());

        FamilyInfoRes response = new FamilyInfoRes(
                family.getFamilyName(),
                family.getFamilyCode(),
                family.getProfileImage(),
                family.getLeaderId(),
                memberInfos
        );

        return ResponseData.ok("가족 정보 조회에 성공했습니다.", response);
    }

    //가족 떠나기
    @Transactional
    public Response leaveFamily(String code) {
        Family family = familyJpaRepo.findById(code)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));
        User user = userSessionHolder.getUser();

        family.getMemberList().remove(user.getId());

        return Response.ok("가족 " + family.getFamilyName() + "을/를 떠나셨습니다.");
    }

    //리더 넘기기
    public Response changeLeader(LeaderChangeReq req) {
        Family family = familyJpaRepo.findById(req.familyCode())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        family.updateLeader(req.id());
        familyJpaRepo.save(family);

        return Response.ok(userJpaRepo.findById(req.id()) + "님에게 리더를 넘겼습니다.");
    }

    @Transactional
    public Response deleteFamily() {
        User currentUser = userSessionHolder.getUser();

        // 가족 정보 조회
        Family family = familyJpaRepo.findByMemberListContaining(currentUser.getId())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        String familyCode = family.getFamilyCode();

        // 리더가 아니면 삭제 권한이 없음
        if (!family.getLeaderId().equals(currentUser.getId())) {
            throw new CustomException(FamilyErrorCode.NOT_FAMILY_LEADER);
        }

        // 가족 구성원 ID 리스트
        List<Long> familyMemberIds = family.getMemberList();

        // 1. ��범 데이터 삭제 (외래키 제약조건 순서대로)
        // 1-1. 댓글 삭제 (먼저 삭제해야 함)
        commentJpaRepo.deleteByFamilyCode(familyCode);
        // 1-2. 이미지 삭제 (그 다음 삭제)
        postImageJpaRepo.deleteByFamilyCode(familyCode);
        // 1-3. 게시글 삭제 (마지막에 삭제)
        postJpaRepo.deleteByFamilyCode(familyCode);

        // 2. 질문/답변 데이터 삭제 (답변부터 먼저 삭제)
        answerJpaRepo.deleteByFamilyCode(familyCode);
        questionJpaRepo.deleteByFamilyCode(familyCode);

        // 3. 편지 데이터 삭제 - 가족 구성원들이 보내거나 받은 편지 모두 삭제
        letterJpaRepo.deleteByFamilyMemberIds(familyMemberIds);

        // 4. 메모 데이터 삭제
        memoJpaRepo.deleteByFamilyCode(familyCode);

        // 5. 캘린더 데이터 삭제
        dDayJpaRepo.deleteByFamilyCode(familyCode);
        scheduleJpaRepo.deleteByFamilyCode(familyCode);

        // 6. 우가 관련 데이터 삭제
        // 6-1. 우가 기여도 삭제
        List<Uga> familyUgas = ugaJpaRepo.findByFamilyCode(familyCode);
        for (Uga uga : familyUgas) {
            ugaContributionJpaRepo.deleteAllByUgaId(uga.getId());
        }
        // 6-2. 우가 삭제
        ugaJpaRepo.deleteByFamilyCode(familyCode);
        // 6-3. 우가 자산(꾸미기 아이템) 삭제
        ugaAssetJpaRepo.deleteById(familyCode);

        // 7. 가족 구성원 정보 초기화
        for (Long memberId : family.getMemberList()) {
            User member = userJpaRepo.findById(memberId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
            member.resetFamily();
            userJpaRepo.save(member);
        }

        // 8. 가족 삭제 (마지막에 삭제)
        familyJpaRepo.delete(family);

        return Response.ok("가족이 삭제되었습니다.");
    }
}