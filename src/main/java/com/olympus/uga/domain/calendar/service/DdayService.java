package com.olympus.uga.domain.calendar.service;

import com.olympus.uga.domain.calendar.domain.Dday;
import com.olympus.uga.domain.calendar.domain.repo.DdayJpaRepo;
import com.olympus.uga.domain.calendar.error.CalendarErrorCode;
import com.olympus.uga.domain.calendar.presentation.dto.request.DdayReq;
import com.olympus.uga.domain.calendar.presentation.dto.request.DdayUpdateReq;
import com.olympus.uga.domain.calendar.presentation.dto.response.DdayListRes;
import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DdayService {
    private final DdayJpaRepo ddayJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final FamilyJpaRepo familyJpaRepo;

    public List<DdayListRes> getList() {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        List<Dday> ddayList = ddayJpaRepo.findByFamilyCodeOrderByDateAsc(userFamilyCode);

        return ddayList.stream()
                .map(DdayListRes::from)
                .toList();
    }

    @Transactional
    public Response createDday(DdayReq req) {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        ddayJpaRepo.save(DdayReq.fromDdayReq(userFamilyCode, req));

        return Response.created(req.title() + "의 디데이를 생성하였습니다.");
    }

    @Transactional
    public Response updateDday(DdayUpdateReq req) {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        Dday dday = ddayJpaRepo.findByIdAndFamilyCode(req.ddayId(), userFamilyCode)
                .orElseThrow(() -> new CustomException(CalendarErrorCode.DDAY_NOT_FOUND));

        dday.updateDday(req.title(), req.date(), req.isHighlight());

        return Response.ok("디데이를 수정하였습니다.");
    }

    @Transactional
    public Response deleteDday(Long ddayId) {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        Dday dday = ddayJpaRepo.findByIdAndFamilyCode(ddayId, userFamilyCode)
                .orElseThrow(() -> new CustomException(CalendarErrorCode.DDAY_NOT_FOUND));

        ddayJpaRepo.delete(dday);

        return Response.ok("디데이를 삭제하였습니다.");
    }

    private String getUserFamilyCode(Long userId) {
        Family family = familyJpaRepo.findByMemberListContaining(userId)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        return family.getFamilyCode();
    }
}