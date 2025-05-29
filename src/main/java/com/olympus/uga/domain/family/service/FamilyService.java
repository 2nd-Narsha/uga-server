package com.olympus.uga.domain.family.service;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.family.presentation.dto.request.FamilyCreateReq;
import com.olympus.uga.domain.family.presentation.dto.response.FamilyInfoRes;
import com.olympus.uga.domain.family.util.CodeGenerator;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.image.service.ImageService;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyRepo familyRepo;
    private final CodeGenerator codeGenerator;
    private final ImageService imageService;
    private final UserJpaRepo userJpaRepo;

    //가족 생성
    @Transactional
    public Response createFamily(MultipartFile familyProfile, FamilyCreateReq familyCreateReq) {
        String code = codeGenerator.generateCode();

        while (familyRepo.findByFamilyCode(code).isPresent()) {
            code = codeGenerator.generateCode();
        }

        Family family = new Family(familyCreateReq, imageService.uploadImage(familyProfile).getImageUrl(), code);
        familyRepo.save(family);
        return Response.created(family.getFamilyCode());
    }

    //가족 가입
    @Transactional
    public Response joinFamily(String familyCode) {
        Family family = familyRepo.findByFamilyCode(familyCode)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));
        family.getMemberList().add(SecurityContextHolder.getContext().getAuthentication().getName());
        return Response.ok("가족 " + family.getFamilyName() + "에 가입하셨습니다.");
    }

    //가족 조회
    public FamilyInfoRes getFamily() {
        Family family = familyRepo.findAll()
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
        Family family = familyRepo.findAll()
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
    public Response changeRepresentative(String familyCode, String phoneNum) {
        familyRepo.findByFamilyCode(familyCode)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND))
                .setRepresentativePhoneNum(phoneNum);
        return Response.ok(userJpaRepo.findById(phoneNum) + "님에게 리더를 넘기셨습니다.");
    }
}
