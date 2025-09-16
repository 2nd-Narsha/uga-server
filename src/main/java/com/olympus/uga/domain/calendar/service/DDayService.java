package com.olympus.uga.domain.calendar.service;

import com.olympus.uga.domain.calendar.domain.DDay;
import com.olympus.uga.domain.calendar.domain.repo.DDayJpaRepo;
import com.olympus.uga.domain.calendar.error.CalendarErrorCode;
import com.olympus.uga.domain.calendar.presentation.dto.request.DDayReq;
import com.olympus.uga.domain.calendar.presentation.dto.request.DDayUpdateReq;
import com.olympus.uga.domain.calendar.presentation.dto.response.DDayListRes;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DDayService {
    private final DDayJpaRepo dDayJpaRepo;
    private final UserSessionHolder userSessionHolder;

    public List<DDayListRes> getList() {
        User user = userSessionHolder.getUser();
        List<DDay> ddays = dDayJpaRepo.findByFamilyCodeOrderByDateAsc(user.getFamilyCode());

        return ddays.stream()
                .map(DDayListRes::from)
                .toList();
    }

    @Transactional
    public Response createDday(DDayReq req) {
        User user = userSessionHolder.getUser();

        dDayJpaRepo.save(DDayReq.fromDdayReq(user.getFamilyCode(), req));

        return Response.created("디데이가 생성되었습니다.");
    }

    @Transactional
    public Response updateDday(DDayUpdateReq req) {
        User user = userSessionHolder.getUser();

        DDay dday = dDayJpaRepo.findById(req.id())
                .orElseThrow(() -> new CustomException(CalendarErrorCode.DDAY_NOT_FOUND));

        if (!dday.getFamilyCode().equals(user.getFamilyCode())) {
            throw new CustomException(CalendarErrorCode.CAN_NOT_UPDATE);
        }

        dday.updateDday(req.title(), req.date(), req.startTime(), req.endTime(), req.isHighlight());

        return Response.ok("디데이가 수정되었습니다.");
    }

    @Transactional
    public Response deleteDday(Long ddayId) {
        User user = userSessionHolder.getUser();

        DDay dday = dDayJpaRepo.findById(ddayId)
                .orElseThrow(() -> new CustomException(CalendarErrorCode.DDAY_NOT_FOUND));

        if (!dday.getFamilyCode().equals(user.getFamilyCode())) {
            throw new CustomException(CalendarErrorCode.CAN_NOT_DELETE);
        }

        dDayJpaRepo.delete(dday);
        return Response.ok("디데이가 삭제되었습니다.");
    }

    // 매일 자정에 만료된 디데이를 삭제하는 스케줄러
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteExpiredDdays() {
        LocalDate today = LocalDate.now();
        List<DDay> expiredDdays = dDayJpaRepo.findByDateBefore(today);

        if (!expiredDdays.isEmpty()) {
            dDayJpaRepo.deleteAll(expiredDdays);
            log.info("만료된 디데이 {}개를 삭제했습니다.", expiredDdays.size());
        }
    }
}
