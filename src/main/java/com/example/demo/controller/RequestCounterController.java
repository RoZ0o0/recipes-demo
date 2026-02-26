package com.example.demo.controller;

import com.example.demo.api.RequestApi;
import com.example.demo.filter.RequestCounterFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RequestCounterController implements RequestApi {

    private final RequestCounterFilter requestCounterFilter;

    @Override
    public ResponseEntity<Long> getRequestCount() {
        return ResponseEntity.ok(requestCounterFilter.getRequestCount());
    }
}
