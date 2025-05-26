package com.olympus.uga.domain.uga.service;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.enums.FoodType;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;
import com.olympus.uga.domain.uga.domain.repo.UgaRepo;
import com.olympus.uga.domain.uga.error.UgaErrorCode;
import com.olympus.uga.domain.uga.presentation.dto.req.UgaCreateReq;
import com.olympus.uga.domain.uga.presentation.dto.req.UgaFeedReq;
import com.olympus.uga.domain.uga.presentation.dto.res.UgaInfoRes;
import com.olympus.uga.domain.uga.presentation.dto.res.UgaListRes;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UgaService {

    private final UgaRepo ugaRepo;
    private final FamilyRepo familyRepo;
    private final UserJpaRepo userJpaRepo;

    @Transactional
    public Response createUga(UgaCreateReq ugaCreateReq) {
        Uga uga = new Uga(ugaCreateReq);

        Family family = familyRepo.findById(ugaCreateReq.getFamilyCode())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        uga.setFamily(family);
        family.setPresentUgaId(uga.getUgaId());

        ugaRepo.save(uga);

        return Response.created("당신의 우가 " + ugaCreateReq.getUgaName() + "가 생성되었습니다.");
    }

    //일단 음식이 존재 하는 지 여부 확인
    //가족 기여도
    public Response ugaFeed(UgaFeedReq ugaFeedReq) {

        Uga uga = ugaRepo.findById(ugaFeedReq.getUgaId())
                .orElseThrow(() -> new CustomException(UgaErrorCode.UGA_NOT_FOUND));

        Map<FoodType, Integer> foodEffect = Map.of(
                FoodType.BANANA_CHIP, 1,
                FoodType.BANANA, 3,
                FoodType.BANANA_KICK, 7
        );

        int minusDays = foodEffect.getOrDefault(ugaFeedReq.getFoodType(), 0);

        if (minusDays == 0) {
            throw new CustomException(UgaErrorCode.INVALID_FOOD_TYPE);
        }

        uga.setCompleteGrowthTime(uga.getCompleteGrowthTime().minusDays(minusDays));

        resetUga(uga);

        return Response.ok("우가에게 먹이를 주었습니다.");
    }

    public UgaInfoRes getUga(Long ugaId) {
        return new UgaInfoRes(resetUga(ugaRepo.findById(ugaId).orElseThrow(() -> new CustomException(UgaErrorCode.UGA_NOT_FOUND))));
    }

    public List<UgaListRes> getUgaList() {

        Family family = familyRepo.findAll().stream()
                .filter(f -> f.getMemberList().contains(SecurityContextHolder.getContext().getAuthentication().getName()))
                .findFirst()
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        return ugaRepo.findByFamily(family).stream()
                .map(UgaListRes::new)
                .toList();
    }

    private Uga resetUga(Uga uga) {
        long hoursLeft = Duration.between(LocalDateTime.now(), uga.getCompleteGrowthTime()).toHours();

        if (hoursLeft <= 180 && hoursLeft >= 90) {
            uga.setGrowth(UgaGrowth.CHILD);
        } else if (hoursLeft <= 270) {
            uga.setGrowth(UgaGrowth.TEENAGER);
        } else if (hoursLeft <= 365) {
            uga.setGrowth(UgaGrowth.ADULT);
        } else if (LocalDateTime.now().isAfter(uga.getCompleteGrowthTime())) {
            uga.setGrowth(UgaGrowth.ALL_GROWTH);
        }

        return uga;
    }
}
