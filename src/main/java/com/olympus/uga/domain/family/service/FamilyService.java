package com.olympus.uga.domain.family.service;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.family.presentation.dto.request.FamilyCreateReq;
import com.olympus.uga.domain.family.presentation.dto.request.LeaderChangeReq;
import com.olympus.uga.domain.family.presentation.dto.response.FamilyInfoRes;
import com.olympus.uga.domain.family.util.CodeGenerator;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.error.UserErrorCode;
import com.olympus.uga.domain.user.util.InterestConverter;
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.image.service.ImageService;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyJpaRepo familyJpaRepo;
    private final CodeGenerator codeGenerator;
    private final ImageService imageService;
    private final UserJpaRepo userJpaRepo;
    private final UserSessionHolder userSessionHolder;

    //가족 생성
    @Transactional
    public ResponseData<String> createFamily(FamilyCreateReq req) {
        User user = userSessionHolder.getUser();
        String code = codeGenerator.generateCode();

        while (familyJpaRepo.findById(code).isPresent()) {
            code = codeGenerator.generateCode();
        }

        familyJpaRepo.save(FamilyCreateReq.fromFamilyCreateReq(code, req, user.getId(), imageService.uploadImage(req.getFamilyProfile()).getImageUrl()));

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
}
