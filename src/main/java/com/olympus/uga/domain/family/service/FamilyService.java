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
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.image.service.ImageService;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseData<String> createFamily(MultipartFile familyProfile, FamilyCreateReq req) {
        User user = userSessionHolder.getUser();
        String code = codeGenerator.generateCode();

        while (familyJpaRepo.findByFamilyCode(code).isPresent()) {
            code = codeGenerator.generateCode();
        }

        familyJpaRepo.save(FamilyCreateReq.fromFamilyCreateReq(code, req, user.getId(), imageService.uploadImage(familyProfile).getImageUrl()));
        return ResponseData.created("가족 생성에 성공했습니다.", code);
    }

    //가족 가입
    @Transactional
    public Response joinFamily(String familyCode) {
        Family family = familyJpaRepo.findByFamilyCode(familyCode)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));
        User user = userSessionHolder.getUser();

        family.getMemberList().add(user.getId());

        return Response.ok("가족 " + family.getFamilyName() + "에 가입이 되었습니다.");
    }

    //가족 조회
    public FamilyInfoRes getFamily() {
        Family family = familyJpaRepo.findAll()
                .stream()
                .filter(f -> f.getMemberList().contains(SecurityContextHolder.getContext().getAuthentication().getName()))
                .findFirst()
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        return new FamilyInfoRes(
                family,
                family.getMemberList().stream()
                        .map(f -> userJpaRepo.findById(f)
                                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER)))
                        .collect(Collectors.toList())
                );
    }

    //가족 떠나기
    @Transactional
    public Response leaveFamily() {
        Family family = familyJpaRepo.findAll()
                .stream()
                .filter(f -> f.getMemberList().contains(SecurityContextHolder.getContext().getAuthentication().getName()))
                .findFirst()
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        if (family.getMemberList().contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
            family.getMemberList().remove(SecurityContextHolder.getContext().getAuthentication().getName());
        } else {
            throw new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER);
        }
        return Response.ok("가족 " + family.getFamilyName() + "을/를 떠나셨습니다.");
    }

    //리더 넘기기
    public Response changeLeader(LeaderChangeReq req) {
        Family family = familyJpaRepo.findByFamilyCode(req.familyCode())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        family.updateLeader(req.id());
        familyJpaRepo.save(family);

        return Response.ok(userJpaRepo.findById(req.id()) + "님에게 리더를 넘겼습니다.");
    }
}
