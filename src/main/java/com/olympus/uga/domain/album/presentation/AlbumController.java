package com.olympus.uga.domain.album.presentation;

import com.olympus.uga.domain.album.presentation.dto.request.CommentReq;
import com.olympus.uga.domain.album.presentation.dto.request.PostReq;
import com.olympus.uga.domain.album.presentation.dto.request.PostUpdateReq;
import com.olympus.uga.domain.album.presentation.dto.response.GalleryRes;
import com.olympus.uga.domain.album.presentation.dto.response.PostListRes;
import com.olympus.uga.domain.album.presentation.dto.response.PostRes;
import com.olympus.uga.domain.album.service.AlbumService;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/album")
public class AlbumController {
    private final AlbumService albumService;

    // 게시글 API
    @GetMapping("/posts")
    public List<PostListRes> getPosts() {
        return albumService.getPosts();
    }

    @GetMapping("/post/{postId}")
    public PostRes getPost(@PathVariable("postId") Long postId) {
        return albumService.getPost(postId);
    }

    @PostMapping("/post/create")
    public Response createPost(@RequestBody PostReq req) {
        return albumService.createPost(req);
    }

    @PatchMapping("/post/update/{postId}")
    public Response updatePost(@PathVariable("postId") Long postId, @RequestBody PostUpdateReq req) {
        return albumService.updatePost(postId, req);
    }

    @DeleteMapping("/post/delete/{postId}")
    public Response deletePost(@PathVariable("postId") Long postId) {
        return albumService.deletePost(postId);
    }

    // 댓글 API
    @PostMapping("/comment/{postId}")
    public Response createComment(@PathVariable("postId") Long postId, @RequestBody CommentReq req) {
        return albumService.createComment(postId, req);
    }

    @DeleteMapping("/comment/delete/{commentId}")
    public Response deleteComment(@PathVariable("commentId") Long commentId) {
        return albumService.deleteComment(commentId);
    }

    // 갤러리 API
    @GetMapping("/gallery")
    public List<GalleryRes> getGallery() {
        return albumService.getGallery();
    }
}
