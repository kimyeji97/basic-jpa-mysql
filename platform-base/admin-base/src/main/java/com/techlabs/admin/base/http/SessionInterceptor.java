package com.techlabs.admin.base.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
@RequiredArgsConstructor
public class SessionInterceptor implements HandlerInterceptor {
    
//    private final JwtTokenProvider jwtTokenProvider;
//
//    private final CompanyRepository companyRepository;
//    private final CompanyEmployeeRepository companyEmployeeRepository;
//    private final CompanyMediaRelRepository companyMediaRelRepository;
    
    @Override
    public boolean preHandle(HttpServletRequest request , HttpServletResponse response , Object handler) throws Exception {
        
        // 토큰에 담길 로그인 유저 정보 Request Attribute에 추가
//        if (ObjectUtils.isNotEmpty(request)) {
//            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
//            if (StringUtils.isNotBlank(token)) {
//                SessionUser sessionUser = jwtTokenProvider.getSessionUserFormClaims(token);
//                this.setUserSubInfo(sessionUser);
//                request.setAttribute(PlatformConst.ATTRIBUTE_SESSION_USER , sessionUser);
//            }
//        }
        
        return HandlerInterceptor.super.preHandle(request , response , handler);
    }
    
//    private void setUserSubInfo(SessionUser sessionUser) {
//        if (ObjectUtils.isEmpty(sessionUser)) {
//            return;
//        }
//
//        Long userId = sessionUser.getId();
//        CompanyEmployee companyEmployee = companyEmployeeRepository.findTopByUserId(userId);
//        if (ObjectUtils.isNotEmpty(companyEmployee)) {
//            Company company = companyRepository.findOneByKey(companyEmployee.getCompanyId());
//            List<CompanyMediaRel> mediaRelList = companyMediaRelRepository.findByCompanyId(companyEmployee.getCompanyId());
//            sessionUser.setCompany(new SessionUser.SessionCompany(company));
//            sessionUser.setEmployee(new SessionUser.SessionCompanyEmployee(companyEmployee));
//            sessionUser.setCompanyMediaList(mediaRelList.stream().map(SessionUser.SessionCompanyMediaRel::new).toList());
//        }
//    }
}
