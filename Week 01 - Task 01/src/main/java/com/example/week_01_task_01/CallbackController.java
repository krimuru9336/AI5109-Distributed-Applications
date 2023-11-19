package com.example.week_01_task_01;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://azure.stinktopf.de", "https://azure.stinktopf.de"})
public class CallbackController {

    private final CallbackRepository callbackRepository;

    public CallbackController(CallbackRepository callbackRepository) {
        this.callbackRepository = callbackRepository;
    }

    @Transactional
    @GetMapping("/callbacks")
    public ResponseEntity<List<Map<String, Object>>> getAllCallbacks() {
        List<Map<String, Object>> callbacks = listAllCallbacksHelper();
        return new ResponseEntity<>(callbacks, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/request-a-callback")
    public ResponseEntity<String> requestACallback(@RequestBody Callback callback) {
        callbackRepository.save(callback);
        return new ResponseEntity<>("Callback requested successfully!", HttpStatus.CREATED);
    }

    @Transactional
    @PostMapping("/list-all-callbacks")
    public ResponseEntity<List<Map<String, Object>>> listAllCallbacks() {
        List<Map<String, Object>> callbacks = listAllCallbacksHelper();
        return new ResponseEntity<>(callbacks, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/clear-all-callbacks")
    public ResponseEntity<String> clearAllCallbacks() {
        callbackRepository.deleteAll();
        return new ResponseEntity<>("All callbacks cleared successfully!", HttpStatus.OK);
    }

    private List<Map<String, Object>> listAllCallbacksHelper() {
        Iterable<Callback> repositoryContents = callbackRepository.findAll();
        List<Map<String, Object>> results = new ArrayList<>();

        for (Callback entity : repositoryContents) {
            Map<String, Object> resultsMap = new HashMap<>();
            resultsMap.put("id", entity.getId());
            resultsMap.put("name", entity.getName());
            resultsMap.put("phone", entity.getPhone());
            results.add(resultsMap);
        }

        return results;
    }
}
