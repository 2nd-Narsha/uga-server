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
import com.olympus.uga.domain.album.presentation.dto.request.PostUpdateReq;
import com.olympus.uga.domain.album.presentation.dto.response.CommentRes;
import com.olympus.uga.domain.album.presentation.dto.response.GalleryRes;
import com.olympus.uga.domain.album.presentation.dto.response.PostListRes;
import com.olympus.uga.domain.album.presentation.dto.response.PostRes;
import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.mission.service.MissionService;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final MissionService missionService;

    // 게시글 서비스 메서드
    @Transactional(readOnly = true)
    public List<PostListRes> getPosts() {
        User user = userSessionHolder.getUser();

        List<Post> postList = postJpaRepo.findByFamilyCodeOrderByCreatedAtDesc(user.getFamilyCode());

        return postList.stream()
                .map(post -> {
                    Long commentCount = commentJpaRepo.countByPostPostId(post.getPostId());
                    List<String> imageUrls = postImageJpaRepo.findByPostPostIdOrderByImageOrder(post.getPostId())
                            .stream()
                            .map(PostImage::getImageUrl)
                            .toList();
                    return PostListRes.from(post, commentCount, imageUrls);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public PostRes getPost(Long postId) {
        User user = userSessionHolder.getUser();

        Post post = postJpaRepo.findByIdAndFamilyCode(postId, user.getFamilyCode())
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

        Family family = familyJpaRepo.findByMemberListContaining(user.getId())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        // 이미지 개수 검증
        if (req.imageUrls() != null && req.imageUrls().size() > 5) {
            throw new CustomException(AlbumErrorCode.TOO_MANY_IMAGES);
        }

        postJpaRepo.save(PostReq.fromPostReq(user, family, req));

        missionService.onAlbumUploaded(user);

        return Response.created("게시글이 생성되었습니다.");
    }

    @Transactional
    public Response updatePost(Long postId, PostUpdateReq req) {
        User user = userSessionHolder.getUser();

        // 본인이 작성한 게시글만 수정 가능
        Post post = postJpaRepo.findByIdAndWriterId(postId, user.getId())
                .orElseThrow(() -> new CustomException(AlbumErrorCode.POST_NOT_FOUND));

        // 이미지 개수 검증
        if (req.imageUrls() != null && req.imageUrls().size() > 5) {
            throw new CustomException(AlbumErrorCode.TOO_MANY_IMAGES);
        }

        // 게시글 내용 수정
        post.updateContent(req.content());

        // 기존 이미지 삭제 후 새 이미지 추가
        postImageJpaRepo.deleteByPostPostId(postId);

        if (req.imageUrls() != null && !req.imageUrls().isEmpty()) {
            List<PostImage> newImages = new ArrayList<>();
            for (int i = 0; i < req.imageUrls().size(); i++) {
                PostImage image = PostImage.builder()
                        .post(post)
                        .imageUrl(req.imageUrls().get(i))
                        .imageOrder(i + 1)
                        .build();
                newImages.add(image);
            }
            postImageJpaRepo.saveAll(newImages);
        }

        return Response.ok("게시글이 수정되었습니다.");
    }

    @Transactional
    public Response deletePost(Long postId) {
        User user = userSessionHolder.getUser();

        Post post = postJpaRepo.findByIdAndWriterId(postId, user.getId())
                .orElseThrow(() -> new CustomException(AlbumErrorCode.POST_NOT_FOUND));

        postJpaRepo.delete(post);

        return Response.ok("게시글이 삭제되었습니다.");
    }

    // 댓글 서비스 메서드
    @Transactional
    public Response createComment(Long postId, CommentReq req) {
        User user = userSessionHolder.getUser();

        Post post = postJpaRepo.findByIdAndFamilyCode(postId, user.getFamilyCode())
                .orElseThrow(() -> new CustomException(AlbumErrorCode.POST_NOT_FOUND));

        commentJpaRepo.save(CommentReq.fromCommentReq(user, post, req));

        missionService.onAlbumCommented(user);

        return Response.created("댓글이 생성되었습니다.");
    }

    @Transactional
    public Response deleteComment(Long commentId) {
        User user = userSessionHolder.getUser();

        Comment comment = commentJpaRepo.findByIdAndWriterId(commentId, user.getId())
                .orElseThrow(() -> new CustomException(AlbumErrorCode.COMMENT_NOT_FOUND));

        commentJpaRepo.delete(comment);

        return Response.ok("댓글이 삭제되었습니다.");
    }

    // 갤러리 서비스 메서드
    public List<GalleryRes> getGallery() {
        User user = userSessionHolder.getUser();

        // 이미지가 있는 게시글들만 가져오기
        List<Post> postsWithImages = postJpaRepo.findPostsWithImagesByFamilyCode(user.getFamilyCode());

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
}
