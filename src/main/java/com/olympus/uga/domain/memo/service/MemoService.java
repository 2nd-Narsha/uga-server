package com.olympus.uga.domain.memo.service;

import com.olympus.uga.domain.memo.domain.repo.MemoRepository;
import com.olympus.uga.domain.memo.presentation.dto.req.LocationUpdateReq;
import com.olympus.uga.domain.memo.presentation.dto.req.MemoCreateReq;
import com.olympus.uga.domain.memo.presentation.dto.res.MemoInfoRes;
import com.olympus.uga.domain.uga.service.UgaService;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final UserJpaRepo userJpaRepo;
    private final UgaService ugaService;

    public Response save(MemoCreateReq req) {
        //메모 생성될때 이전 메모는 자동 삭제되게 해야 함
        //조회 했다 내용 삭제 해야 함
        User user = userSessionHolder.getUser();

        memoJpaRepo.save(MemoCreateReq.fromMemoCreateReq(user, req));

        return Response.created("메모가 성공적으로 생성되었습니다.");
    }

    public Response updateLocation(LocationUpdateReq req) {
        User user = userSessionHolder.getUser();
        memoJpaRepo.findByWriter(user)
                .orElseThrow()
                .updateLocation(req.location());

        return Response.ok("위치가 성공적으로 저장되었습니다.");
    }

    //조회시 조회 했다 표시해야 함
    public MemoInfoRes getOne(Long userId) {
        return userJpaRepo.findById(userId)
                .flatMap(memoJpaRepo::findByWriter)
                .map(memo -> MemoInfoRes.from(
                        memo.getId(),
                        memo.getWriter(),
                        ugaService.getCurrentUga().myContributionRate(),
                        memo.getContent(),
                        memo.getLocation()
                ))
                .orElseThrow();
    }

    public Boolean checked(Long userId) {
        User user = userSessionHolder.getUser();
    }
}
