package com.olympus.uga.domain.album.service;

import com.olympus.uga.domain.album.domain.Post;
import com.olympus.uga.domain.album.domain.repo.PostJpaRepo;
import com.olympus.uga.domain.album.error.PostErrorCode;
import com.olympus.uga.domain.album.presentation.dto.request.PostReq;
import com.olympus.uga.domain.album.presentation.dto.response.PostListRes;
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
public class AlbumService {
    private final PostJpaRepo postJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final FamilyJpaRepo familyJpaRepo;

    public List<PostListRes> getPosts() {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        List<Post> postList = postJpaRepo.findByFamilyCodeOrderByCreatedAtDesc(userFamilyCode);

        return postList.stream()
                .map(PostListRes::from)
                .toList();
    }

    @Transactional
    public Response createPost(PostReq req) {
        User user = userSessionHolder.getUser();

        postJpaRepo.save(PostReq.fromPostReq(user, req));

        return Response.created("게시글이 생성되었습니다.");
    }

    @Transactional
    public Response deletePost(Long postId) {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        Post post = postJpaRepo.findByIdAndFamilyCode(postId, userFamilyCode)
                        .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));

        postJpaRepo.delete(post);

        return Response.ok("게시글이 삭제되었습니다.");
    }

    private String getUserFamilyCode(Long userId) {
        Family family = familyJpaRepo.findByMemberListContaining(userId)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        return family.getFamilyCode();
    }
}
