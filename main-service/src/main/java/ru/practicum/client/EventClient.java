//package ru.practicum.client;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.web.util.DefaultUriBuilderFactory;
//import ru.practicum.BaseClient;
//import ru.practicum.Constant;
//import ru.practicum.dto.EndpointHitDto;
//
//import javax.servlet.http.HttpServletRequest;
//import java.time.LocalDateTime;
//
//import static org.springframework.http.RequestEntity.post;
//
//
//@Service
//public class EventClient extends BaseClient {
//
//    @Autowired
//    public EventClient(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
//        super(
//                builder
//                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
//                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
//                        .build()
//        );
//    }
//
//    public void createHit(HttpServletRequest request) {
//        String uri = request.getRequestURI();
//        String ip = request.getRemoteAddr();
//        post("/hit", EndpointHitDto.builder()
//                .ip(ip)
//                .uri(uri)
//                .app("ewm-main-service")
//                .timestamp(LocalDateTime.now().format(Constant.FORMATTER))
//                .build());
//    }
//}