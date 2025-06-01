//package com.boanni_back.project.admin.controller;
//
//import com.boanni_back.project.admin.controller.dto.AdminDeadlineDto;
//import com.boanni_back.project.admin.service.AdminDeadlineService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/admin/users/deadline")
//public class AdminDeadlineController {
//
//    private final AdminDeadlineService adminDeadlineService;
//
//    //해당 id 회원의 start/end date 확인 매핑
//    @GetMapping("/{id}")
//    public ResponseEntity<AdminDeadlineDto> getDeadline(@PathVariable Long id) {
//        return ResponseEntity.ok(adminDeadlineService.getDeadline(id));
//    }
//
//    //해당 id 회원의 마감 기한 입력 매핑
//    @PatchMapping("/{id}")
//    public ResponseEntity<Void> updateDeadline(@PathVariable Long id,
//                                               @RequestBody AdminDeadlineDto dto) {
//        adminDeadlineService.setDeadline(id, dto);
//        return ResponseEntity.ok().build();
//    }
//}
//
