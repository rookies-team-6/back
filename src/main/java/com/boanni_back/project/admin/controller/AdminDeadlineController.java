package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.controller.dto.AdminDeadlineDto;
import com.boanni_back.project.admin.service.AdminDeadlineService;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users/deadline")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminDeadlineController {

    private final AdminDeadlineService adminDeadlineService;

    //수정 form view
    @GetMapping("/{id}/edit")
    public String getEditDeadlineForm(@PathVariable Long id, Model model) {
        AdminDeadlineDto.Response dto = adminDeadlineService.getDeadline(id);
        model.addAttribute("deadlineDto", dto);
        return "admin/deadline/edit";
    }

    //해당 id 회원의 마감일 수정
    @PatchMapping("/{id}")
    public String updateDeadline(@PathVariable Long id,
                                 @ModelAttribute("deadlineDto") AdminDeadlineDto.Request request,
                                 RedirectAttributes redirectAttributes) {
        request.setUserId(id);
        try {
            adminDeadlineService.updateDeadline(request);
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users/deadline/" + id;
        }
        redirectAttributes.addFlashAttribute("successMessage", "마감일이 성공적으로 수정되었습니다.");
        return "redirect:/admin/users";
    }

    //api 용 마감 수정일 매핑
    @PatchMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<AdminDeadlineDto.Response> updateDeadlineApi(@PathVariable Long id,
                                                                       @RequestBody AdminDeadlineDto.Request request) {
        request.setUserId(id);

        try {
            Users updatedUser = adminDeadlineService.updateDeadline(request);
            LocalDate oldDeadline = request.getNewDeadline();

            AdminDeadlineDto.Response response =
                    AdminDeadlineDto.Response.fromEntity(updatedUser, oldDeadline);

            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //학습 마감일이 지난 회원 목록 조회
    @GetMapping("/expired")
    public String getExpiredUsers(Model model) {
        List<AdminDeadlineDto.Response> expired = adminDeadlineService.getUsersWithExpiredDeadline();
        model.addAttribute("expiredUsers", expired);
        return "admin/deadline/expired";
    }
}