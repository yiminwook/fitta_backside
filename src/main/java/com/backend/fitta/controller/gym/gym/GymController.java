package com.backend.fitta.controller.gym.gym;

import com.backend.fitta.dto.gym.FindGymByIdResponse;
import com.backend.fitta.dto.gym.SaveGymRequest;
import com.backend.fitta.dto.gym.UpdateGymRequest;
import com.backend.fitta.service.interfaces.GymApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "헬스장", description = "헬스장 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/gym")
public class GymController {
    private final GymApiService gymApiService;

    @Operation(summary = "헬스장 추가 메서드", description = "헬스장 추가 메서드입니다.")
    @PostMapping
    public ResponseEntity<Long> saveGym(@Valid @RequestBody SaveGymRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gymApiService.save(request));
    }


    @Operation(summary = "헬스장 조회 메서드", description = "헬스장 id로 헬스장 정보를 조회 할 수 있습니다.")
    @GetMapping("/{gymId}")
    public ResponseEntity<FindGymByIdResponse> findGym(@PathVariable long gymId) {
        return ResponseEntity.ok(gymApiService.findById(gymId));
    }

    @Operation(summary = "헬스장 정보 수정 메서드", description = "헬스장 id로 헬스장 정보를 찾아 헬스장 정보를 수정 할 수 있습니다.")
    @PutMapping("/{gymId}")
    public ResponseEntity<Long> updateGym(@PathVariable Long gymId, @Valid @RequestBody UpdateGymRequest request) {
        return ResponseEntity.ok(gymApiService.update(gymId, request));
    }

    @Operation(summary = "헬스장 삭제 메서드", description = "헬스장 id로 헬스장을 삭제할 수 있습니다.")
    @DeleteMapping("/{gymId}")
    public ResponseEntity<Void> deleteGym(@PathVariable Long gymId) {
        gymApiService.delete(gymId);
        return ResponseEntity.noContent().build();
    }

}
