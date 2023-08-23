package joo.project.my3d.service;

import joo.project.my3d.dto.response.BusinessCertificationResponse;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final RestTemplate restTemplate;

    @Value("${nts.service-key}")
    private String serviceKey;
    private final String NtsApiUrl =  "https://api.odcloud.kr/api/nts-businessman/v1/status";

    /**
     * 국세청 API를 활용한 사업자 인증 로직<br>
     * b_stt (01: 계속사업자, 02: 휴업자, 03: 폐업자)
     */
    public String businessCertification(String b_no) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(NtsApiUrl)
                .queryParam("serviceKey", serviceKey)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject body = new JSONObject();
        body.put("b_no", new String[]{b_no});

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        ResponseEntity<BusinessCertificationResponse> response = restTemplate.exchange(uri, HttpMethod.POST, entity, BusinessCertificationResponse.class);

        return Optional.ofNullable(response.getBody())
                .orElseGet(BusinessCertificationResponse::empty).data().get(0).b_stt_cd();
    }
}