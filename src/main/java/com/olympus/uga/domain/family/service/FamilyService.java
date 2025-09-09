package com.olympus.uga.domain.family.service;

import com.olympus.uga.domain.album.domain.repo.CommentJpaRepo;
import com.olympus.uga.domain.album.domain.repo.PostImageJpaRepo;
import com.olympus.uga.domain.album.domain.repo.PostJpaRepo;
import com.olympus.uga.domain.answer.domain.repo.AnswerJpaRepo;
import com.olympus.uga.domain.attend.domain.repo.AttendJpaRepo;
import com.olympus.uga.domain.calendar.domain.repo.DdayJpaRepo;
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
    private final DdayJpaRepo ddayJpaRepo;
    private final ScheduleJpaRepo scheduleJpaRepo;
    private final PostImageJpaRepo postImageJpaRepo;
    private final CommentJpaRepo commentJpaRepo;
    private final LetterJpaRepo letterJpaRepo;
    private final QuestionJpaRepo questionJpaRepo;
    private final AnswerJpaRepo answerJpaRepo;
    private final MemoJpaRepo memoJpaRepo;
    private final AttendJpaRepo attendJpaRepo;

    //가족 생성
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

    //가족 가입
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

    //가족 조회
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
                            memberUser.getProfileImage(),
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

        Family family = familyJpaRepo.findByMemberListContaining(currentUser.getId())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        String familyCode = family.getFamilyCode();

        // 1. 출석체크 데이터 삭제
        attendJpaRepo.deleteByFamilyCode(familyCode);

        // 2. 우가 관련 데이터 삭제
        // 2-1. 우가 기여도 삭제
        List<Uga> familyUgas = ugaJpaRepo.findByFamilyCode(familyCode);
        for (Uga uga : familyUgas) {
            ugaContributionJpaRepo.deleteAllByUgaId(uga.getId());
        }
        // 2-2. 우가 삭제
        ugaJpaRepo.deleteByFamilyCode(familyCode);
        // 2-3. 우가 자산(꾸미기 아이템) 삭제
        ugaAssetJpaRepo.deleteById(familyCode);

        // 3. 앨범 데이터 삭제 (순서 중요)
        // 3-1. 댓글 삭제
        commentJpaRepo.deleteByFamilyCode(familyCode);
        // 3-2. 이미지 삭제
        postImageJpaRepo.deleteByFamilyCode(familyCode);
        // 3-3. 게시글 삭제
        postJpaRepo.deleteByFamilyCode(familyCode);

        // 4. 캘린더 데이터 삭제
        // 4-1. 디데이 삭제
        ddayJpaRepo.deleteByFamilyCode(familyCode);
        // 4-2. 일정 삭제
        scheduleJpaRepo.deleteByFamilyCode(familyCode);

        // 5. 편지 삭제
        letterJpaRepo.deleteByFamilyCode(familyCode);

        // 6. 질문/답변 삭제
        // 6-1. 답변 먼저 삭제
        answerJpaRepo.deleteByFamilyCode(familyCode);
        // 6-2. 질문 삭제
        questionJpaRepo.deleteByFamilyCode(familyCode);

        // 7. 메모 삭제
        memoJpaRepo.deleteByFamilyCode(familyCode);

        // 8. 마지막으로 가족 삭제
        familyJpaRepo.delete(family);

        return Response.ok("가족이 성공적으로 삭제되었습니다");
    }
}