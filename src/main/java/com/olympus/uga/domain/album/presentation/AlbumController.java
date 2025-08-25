package com.olympus.uga.domain.album.presentation;

import com.olympus.uga.domain.album.presentation.dto.request.PostReq;
import com.olympus.uga.domain.album.presentation.dto.response.PostListRes;
import com.olympus.uga.domain.album.presentation.dto.response.PostRes;
import com.olympus.uga.domain.album.service.AlbumService;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/posts")
    public List<PostListRes> getPosts() {
        return albumService.getPosts();
    }

    @GetMapping("/posts/{postId}")
    public PostRes getPost(@PathVariable("postId") Long postId) {
        return albumService.getPost(postId);
    }

    @PostMapping("/create")
    public Response createPost(@RequestBody PostReq req) {
        return albumService.createPost(req);
    }

    @DeleteMapping("delete/{postId}")
    public Response deletePost(@PathVariable("postId") Long postId) {
        return albumService.deletePost(postId);
    }
}
