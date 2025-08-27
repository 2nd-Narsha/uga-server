package com.olympus.uga.domain.album.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AlbumErrorCode implements CustomErrorCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    TOO_MANY_IMAGES(HttpStatus.BAD_REQUEST, "이미지는 최대 5개까지 업로드할 수 있습니다."),
    UNAUTHORIZED_POST_ACCESS(HttpStatus.FORBIDDEN, "게시글에 대한 권한이 없습니다."),
    UNAUTHORIZED_COMMENT_ACCESS(HttpStatus.FORBIDDEN, "댓글에 대한 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;
}