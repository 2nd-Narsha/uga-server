package com.olympus.uga.domain.uga.service;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.enums.FoodType;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;
import com.olympus.uga.domain.uga.domain.repo.UgaRepo;
import com.olympus.uga.domain.uga.error.UgaErrorCode;
import com.olympus.uga.domain.uga.presentation.dto.request.*;
import com.olympus.uga.domain.uga.presentation.dto.response.*;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.error.UserErrorCode;
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
    private final FamilyJpaRepo familyRepo;
    private final UserJpaRepo userJpaRepo;

    //우가 생성
    @Transactional
    public Response createUga(UgaCreateReq req) {
        Family family = familyRepo.findById(req.familyCode())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        uga.setFamily(family);
        family.setPresentUgaId(uga.getUgaId());
        family.getMemberList().forEach(m -> {userJpaRepo.findById(m).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND)).setContribution(0);});

        ugaRepo.save(uga);

        return Response.created("당신의 우가 " + req.ugaName() + "가 생성되었습니다.");
    }

    //먹이 주기
    public Response ugaFeed(UgaFeedReq req) {
        User user = userJpaRepo.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        FoodType foodType = req.foodType();

        if (!user.getFoods().contains(foodType)) {
            throw new CustomException(UgaErrorCode.FOOD_SHORTAGE);
        }

        user.getFoods().remove(foodType);

        Uga uga = ugaRepo.findById(ugaFeedReq.getUgaId())
                .orElseThrow(() -> new CustomException(UgaErrorCode.UGA_NOT_FOUND));

        Map<FoodType, Integer> foodEffect = Map.of(
                FoodType.BANANA_CHIP, 1,
                FoodType.BANANA, 3,
                FoodType.BANANA_KICK, 7
        );

        int minusDays = foodEffect.getOrDefault(foodType, 0);

        if (minusDays == 0) {
            throw new CustomException(UgaErrorCode.INVALID_FOOD_TYPE);
        }

        user.setContribution(user.getContribution() + minusDays);
        uga.setCompleteGrowthTime(uga.getCompleteGrowthTime().minusDays(minusDays));
        resetUga(uga);

        return Response.ok("우가에게 먹이를 주었습니다.");
    }

    //우가 하나 조회
    public UgaInfoRes getUga(Long ugaId) {
        return new UgaInfoRes(resetUga(ugaRepo.findById(ugaId).orElseThrow(() -> new CustomException(UgaErrorCode.UGA_NOT_FOUND))));
    }

    //본인 가족의 우가 리스트 조회
    public List<UgaListRes> getUgaList() {

        Family family = familyRepo.findAll().stream()
                .filter(f -> f.getMemberList().contains(SecurityContextHolder.getContext().getAuthentication().getName()))
                .findFirst()
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        return ugaRepo.findByFamily(family).stream()
                .map(UgaListRes::new)
                .toList();
    }

    //우가 성장도 설정
    private Uga resetUga(Uga uga) {
        long hoursLeft = Duration.between(LocalDateTime.now(), uga.getCompleteGrowthTime()).toHours();

        if (hoursLeft <= 180 && hoursLeft >= 90) {
            uga.getGrowth(UgaGrowth.CHILD);
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
