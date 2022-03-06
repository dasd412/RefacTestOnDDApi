/*
 * @(#)ProfileRestController.java        1.1.2 2022/3/5
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security.profile;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.controller.exception.PasswordConfirmException;
import com.dasd412.remake.api.controller.security.profile.dto.PasswordUpdateRequestDTO;
import com.dasd412.remake.api.controller.security.profile.dto.ProfileUpdateRequestDTO;
import com.dasd412.remake.api.controller.security.profile.dto.ProfileUpdateResponseDTO;
import com.dasd412.remake.api.controller.security.profile.dto.WithdrawalResponseDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.profile.Profile;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.service.domain.UpdateDeleteDiaryService;
import com.dasd412.remake.api.service.security.WriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 프로필 갱신과 회원 탈퇴를 처리하는 RestController
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 5일
 */
@RestController
public class ProfileRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UpdateDeleteDiaryService updateDeleteDiaryService;
    private final WriterService writerService;


    public ProfileRestController(UpdateDeleteDiaryService updateDeleteDiaryService, WriterService writerService) {
        this.updateDeleteDiaryService = updateDeleteDiaryService;
        this.writerService = writerService;
    }

    /**
     * 회원 프로필 수정
     *
     * @param principalDetails 작성자 인증 정보
     * @param dto              변경할 내용이 담긴 dto
     * @return 정상 변경됬는지 여부
     */
    @PutMapping("/profile/info")
    public ApiResult<ProfileUpdateResponseDTO> updateProfile(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody ProfileUpdateRequestDTO dto) {
        logger.info("update profile info..." + dto.toString());
        Profile updated = updateDeleteDiaryService.updateProfile(EntityId.of(Writer.class, principalDetails.getWriter().getId()), dto.getDiabetesPhase());
        return ApiResult.OK(new ProfileUpdateResponseDTO(updated));
    }

    /**
     * 사용자 회원 탈퇴
     *
     * @param principalDetails 작성자 인증 정보
     * @return 탈퇴 응답 dto
     */
    @DeleteMapping("/profile/withdrawal")
    public ApiResult<WithdrawalResponseDTO> withDrawWriter(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        logger.info("withdraw writer ...!");
        writerService.withdrawWriter(EntityId.of(Writer.class, principalDetails.getWriter().getId()));

        /* SecurityContextHolder 내의 컨텍스트들을 초기화해줘야 로그아웃 처리된다. */
        SecurityContextHolder.clearContext();

        return ApiResult.OK(new WithdrawalResponseDTO(principalDetails.getWriter().getId()));
    }

    /**
     * 기존 비밀 번호 변경
     *
     * @param principalDetails 작성자 인증 정보
     * @param dto              (비밀 번호, 비밀 번호 확인)의 dto
     * @return 변경 정상으로 됬는지 여부
     */
    @PutMapping("/profile/password")
    public ApiResult<?> updatePassword(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody @Valid PasswordUpdateRequestDTO dto) {
        logger.info("update password of user");

        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            return ApiResult.ERROR(new PasswordConfirmException("비밀 번호와 비밀 번호 확인이 동일하지 않습니다."), HttpStatus.BAD_REQUEST);
        }

        writerService.updatePassword(EntityId.of(Writer.class, principalDetails.getWriter().getId()), dto.getPassword());

        return ApiResult.OK("비밀 번호 변경 완료!");
    }
}
