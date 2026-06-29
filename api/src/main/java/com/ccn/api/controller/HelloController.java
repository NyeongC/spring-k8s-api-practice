package com.ccn.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello() {
        String podName = System.getenv("POD_NAME");
        return ResponseEntity.ok(Map.of(
                "message", "Hello from Kubernetes!",
                "pod", podName != null ? podName : "unknown"
        ));
    }

    // CPU 부하 테스트용: seconds 동안 모든 코어를 태움
    @GetMapping("/cpu-burn")
    public ResponseEntity<Map<String, Object>> cpuBurn(
            @RequestParam(defaultValue = "10") int seconds) {

        int cores = Runtime.getRuntime().availableProcessors();
        long durationMs = (long) seconds * 1000;
        long end = System.currentTimeMillis() + durationMs;

        Thread[] threads = new Thread[cores];
        for (int i = 0; i < cores; i++) {
            threads[i] = new Thread(() -> {
                while (System.currentTimeMillis() < end) {
                    Math.sqrt(Math.random());
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException ignored) {}
        }

        return ResponseEntity.ok(Map.of(
                "message", "CPU burned",
                "seconds", seconds,
                "cores", cores
        ));
    }
}
