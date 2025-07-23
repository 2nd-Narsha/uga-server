package com.olympus.uga.domain.family.presentation;

import com.olympus.uga.domain.family.presentation.dto.request.FamilyCreateReq;
import com.olympus.uga.domain.family.presentation.dto.request.LeaderChangeReq;
import com.olympus.uga.domain.family.presentation.dto.response.FamilyInfoRes;
import com.olympus.uga.domain.family.service.FamilyService;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/family")
public class FamilyController {
    private final FamilyService familyService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "가족 생성",
            description = """
                가족을 생성합니다.<br><br>
                <b>Swagger에서 에러가 발생할 경우</b> 아래와 같이 <code>curl</code> 명령어를 사용해 터미널에서 테스트해주세요:
                <pre>
                curl -v -X POST "http://3.39.96.216/family/create" \\
                  -H "Authorization: Bearer {액세스 토큰}" \\
                  -F "familyProfile=@{이미지 파일 경로}" \\
                  -F 'req={\"familyName\":\"가족이름\"};type=application/json'
                </pre>
                <br>
                ⚠️ Swagger에서는 multipart/form-data + JSON 조합이 정상 동작하지 않을 수 있습니다.""")
    public ResponseData<String> createFamily(
            @RequestPart(name = "req") FamilyCreateReq req) {
        return familyService.createFamily(req);
    }

    @PostMapping("/join")
    @Operation(summary = "가족 가입")
    public Response joinFamily(@RequestParam String familyCode) {
        return familyService.joinFamily(familyCode);
    }

    @GetMapping
    @Operation(summary = "가족 정보 불러오기")
    public ResponseData<FamilyInfoRes> getFamily() {
        return familyService.getFamily();
    }

    @PostMapping("/leave")
    @Operation(summary = "가족 떠나기")
    public Response leaveFamily(String familyCode) {
        return familyService.leaveFamily(familyCode);
    }

    @PostMapping("/change-leader")
    @Operation(summary = "가족 리더 넘기기", description = "id: 넘겨줄 구성원의 아이디")
    public Response changeLeader(@RequestBody LeaderChangeReq req) {
        return familyService.changeLeader(req);
    }
}
