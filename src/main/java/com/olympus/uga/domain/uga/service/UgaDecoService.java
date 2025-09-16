package com.olympus.uga.domain.uga.service;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.UgaAsset;
import com.olympus.uga.domain.uga.domain.enums.CharacterType;
import com.olympus.uga.domain.uga.domain.enums.ColorType;
import com.olympus.uga.domain.uga.domain.repo.UgaAssetJpaRepo;
import com.olympus.uga.domain.uga.domain.repo.UgaJpaRepo;
import com.olympus.uga.domain.uga.error.UgaErrorCode;
import com.olympus.uga.domain.uga.presentation.dto.response.UgaDecoRes;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UgaDecoService {
    private final UgaAssetJpaRepo ugaAssetJpaRepo;
    private final UserJpaRepo userJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final FamilyJpaRepo familyJpaRepo;
    private final UgaJpaRepo ugaJpaRepo;

    @Transactional
    public Response purchaseColor(ColorType req) {
        User user = userSessionHolder.getUser();

        if (req == ColorType.DEFAULT) {
            throw new CustomException(UgaErrorCode.CANNOT_PURCHASE_DEFAULT_ITEM);
        }

        // 가족 자산 조회 또는 생성
        UgaAsset ugaAsset = ugaAssetJpaRepo.findById(user.getFamilyCode())
                .orElse(UgaAsset.createDefault(user.getFamilyCode()));

        if (ugaAsset.hasColor(req)) {
            throw new CustomException(UgaErrorCode.ALREADY_OWNED_ITEM);
        }

        user.usePoint(20);
        userJpaRepo.save(user);

        ugaAsset.addColor(req);
        ugaAssetJpaRepo.save(ugaAsset);

        return Response.ok("색상을 성공적으로 구매했습니다.");
    }

    @Transactional
    public Response purchaseCharacter(CharacterType req) {
        User user = userSessionHolder.getUser();

        if (req== CharacterType.UGA) {
            throw new CustomException(UgaErrorCode.CANNOT_PURCHASE_DEFAULT_ITEM);
        }

        UgaAsset ugaAsset = ugaAssetJpaRepo.findById(user.getFamilyCode())
                .orElse(UgaAsset.createDefault(user.getFamilyCode()));

        if (ugaAsset.hasCharacter(req)) {
            throw new CustomException(UgaErrorCode.ALREADY_OWNED_ITEM);
        }

        user.usePoint(200);
        userJpaRepo.save(user);

        ugaAsset.addCharacter(req);
        ugaAssetJpaRepo.save(ugaAsset);

        return Response.ok("캐릭터를 성공적으로 구매했습니다.");
    }

    @Transactional
    public Response changeColor(ColorType req) {
        User user = userSessionHolder.getUser();

        Family family = familyJpaRepo.findById(user.getFamilyCode())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        if (family.getPresentUgaId() == null) {
            throw new CustomException(UgaErrorCode.UGA_NOT_FOUND);
        }

        Uga currentUga = ugaJpaRepo.findById(family.getPresentUgaId())
                .orElseThrow(() -> new CustomException(UgaErrorCode.UGA_NOT_FOUND));

        // 가족 자산 확인
        UgaAsset ugaAsset = ugaAssetJpaRepo.findById(user.getFamilyCode())
                .orElse(UgaAsset.createDefault(user.getFamilyCode()));

        if (!ugaAsset.hasColor(req)) {
            throw new CustomException(UgaErrorCode.NOT_OWNED_ITEM);
        }

        currentUga.updateColor(req);
        ugaJpaRepo.save(currentUga);

        return Response.ok("우가 색상이 성공적으로 변경되었습니다.");
    }

    @Transactional
    public Response changeCharacter(CharacterType req) {
        User user = userSessionHolder.getUser();

        // 현재 우가 조회
        Family family = familyJpaRepo.findById(user.getFamilyCode())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        if (family.getPresentUgaId() == null) {
            throw new CustomException(UgaErrorCode.UGA_NOT_FOUND);
        }

        Uga currentUga = ugaJpaRepo.findById(family.getPresentUgaId())
                .orElseThrow(() -> new CustomException(UgaErrorCode.UGA_NOT_FOUND));

        // 가족 자산 확인
        UgaAsset ugaAsset = ugaAssetJpaRepo.findById(user.getFamilyCode())
                .orElse(UgaAsset.createDefault(user.getFamilyCode()));

        if (!ugaAsset.hasCharacter(req)) {
            throw new CustomException(UgaErrorCode.NOT_OWNED_ITEM);
        }

        currentUga.updateCharacter(req);
        ugaJpaRepo.save(currentUga);

        return Response.ok("우가 캐릭터가 성공적으로 변경되었습니다.");
    }

    @Transactional(readOnly = true)
    public UgaDecoRes getDecoItems() {
        User user = userSessionHolder.getUser();

        // 가족 자산 조회 또는 기본값 생성
        UgaAsset ugaAsset = ugaAssetJpaRepo.findById(user.getFamilyCode())
                .orElse(UgaAsset.createDefault(user.getFamilyCode()));

        return UgaDecoRes.from(ugaAsset);
    }
}
