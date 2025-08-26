package com.olympus.uga.domain.album.service;

import com.olympus.uga.domain.album.domain.Comment;
import com.olympus.uga.domain.album.domain.Post;
import com.olympus.uga.domain.album.domain.PostImage;
import com.olympus.uga.domain.album.domain.repo.CommentJpaRepo;
import com.olympus.uga.domain.album.domain.repo.PostImageJpaRepo;
import com.olympus.uga.domain.album.domain.repo.PostJpaRepo;
import com.olympus.uga.domain.album.error.AlbumErrorCode;
import com.olympus.uga.domain.album.presentation.dto.request.CommentReq;
import com.olympus.uga.domain.album.presentation.dto.request.PostReq;
import com.olympus.uga.domain.album.presentation.dto.response.CommentRes;
import com.olympus.uga.domain.album.presentation.dto.response.GalleryRes;
import com.olympus.uga.domain.album.presentation.dto.response.PostListRes;
import com.olympus.uga.domain.album.presentation.dto.response.PostRes;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final PostJpaRepo postJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final FamilyJpaRepo familyJpaRepo;
    private final CommentJpaRepo commentJpaRepo;
    private final PostImageJpaRepo postImageJpaRepo;

    // 게시글 관련 서비스 메서드
    public List<PostListRes> getPosts() {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        List<Post> postList = postJpaRepo.findByFamilyCodeOrderByCreatedAtDesc(userFamilyCode);

        return postList.stream()
                .map(post -> {
                    Long commentCount = commentJpaRepo.countByPostId(post.getPostId());
                    List<String> imageUrls = postImageJpaRepo.findByPostPostIdOrderByImageOrder(post.getPostId())
                            .stream()
                            .map(PostImage::getImageUrl)
                            .toList();
                    return PostListRes.from(post, commentCount, imageUrls);
                })
                .toList();
    }

    public PostRes getPost(Long postId) {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        Post post = postJpaRepo.findByIdAndFamilyCode(postId, userFamilyCode)
                .orElseThrow(() -> new CustomException(AlbumErrorCode.POST_NOT_FOUND));

        List<CommentRes> comments = commentJpaRepo.findByPostPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentRes::from)
                .toList();

        List<String> imageUrls = postImageJpaRepo.findByPostPostIdOrderByImageOrder(postId)
                .stream()
                .map(PostImage::getImageUrl)
                .toList();

        return PostRes.from(post, comments, imageUrls);
    }

    @Transactional
    public Response createPost(PostReq req) {
        User user = userSessionHolder.getUser();

        // 이미지 개수 검증
        if (req.imageUrls() != null && req.imageUrls().size() > 5) {
            throw new CustomException(AlbumErrorCode.TOO_MANY_IMAGES);
        }

        postJpaRepo.save(PostReq.fromPostReq(user, req));

        return Response.created("게시글이 생성되었습니다.");
    }

    @Transactional
    public Response deletePost(Long postId) {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        Post post = postJpaRepo.findByIdAndFamilyCode(postId, userFamilyCode)
                        .orElseThrow(() -> new CustomException(AlbumErrorCode.POST_NOT_FOUND));

        // 본인 확인
        if (!Objects.equals(post.getWriter().getId(), user.getId())) {
            throw new CustomException(AlbumErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        postJpaRepo.delete(post);

        return Response.ok("게시글이 삭제되었습니다.");
    }

    // 댓글 관련 서비스 메서드
    @Transactional
    public Response createComment(Long postId, CommentReq req) {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        Post post = postJpaRepo.findByIdAndFamilyCode(postId, userFamilyCode)
                .orElseThrow(() -> new CustomException(AlbumErrorCode.POST_NOT_FOUND));

        commentJpaRepo.save(CommentReq.fromCommentReq(user, post, req));

        return Response.created("댓글이 생성되었습니다.");
    }

    @Transactional
    public Response deleteComment(Long commentId) {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        Comment comment = commentJpaRepo.findByIdAndFamilyCode(commentId, userFamilyCode)
                .orElseThrow(() -> new CustomException(AlbumErrorCode.COMMENT_NOT_FOUND));

        // 본인 확인
        if (!Objects.equals(comment.getWriter().getId(), user.getId())) {
            throw new CustomException(AlbumErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
        }

        commentJpaRepo.delete(comment);

        return Response.ok("댓글이 삭제되었습니다.");
    }

    // 갤러리 서비스 메서드
    public List<GalleryRes> getGallery() {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        // 이미지가 있는 게시글들만 가져오기
        List<Post> postsWithImages = postJpaRepo.findPostsWithImagesByFamilyCode(userFamilyCode);

        // 날짜별로 그룹핑
        Map<LocalDate, List<PostImage>> imagesByDate = postsWithImages.stream()
                .flatMap(post -> post.getImages().stream())
                .collect(Collectors.groupingBy(
                        image -> image.getPost().getCreatedAt(),
                        TreeMap::new, // 날짜 순 정렬
                        Collectors.toList()
                ));

        // GalleryRes로 변환
        return imagesByDate.entrySet().stream()
                .sorted(Map.Entry.<LocalDate, List<PostImage>>comparingByKey().reversed()) // 최신 날짜부터
                .map(entry -> GalleryRes.from(entry.getKey(), entry.getValue()))
                .toList();
    }

    private String getUserFamilyCode(Long userId) {
        Family family = familyJpaRepo.findByMemberListContaining(userId)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        return family.getFamilyCode();
    }
}
