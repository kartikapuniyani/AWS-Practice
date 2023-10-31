package com.aws.practice.controller;

import com.aws.practice.service.AwsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/aws")
public class AwsController {

    @Autowired
    private AwsService awsService;

    @PostMapping
    public ResponseEntity<Void> createService(@RequestBody List<String> services) {
        awsService.createService(services);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<String>> getData(@PathVariable String type) {
        return ResponseEntity.ok(awsService.getData(type));
    }

    @GetMapping("/s3/bucket/{name}/files")
    public ResponseEntity<String> getS3Data(@PathVariable String name) {
        return ResponseEntity.ok(awsService.getS3Data(name));
    }

    @GetMapping("/s3/bucket/{name}/count")
    public ResponseEntity<Integer> getS3FilesCount(@PathVariable String name) {
        return ResponseEntity.ok(awsService.getS3FilesCount(name));
    }

    @GetMapping("/s3/bucket/{name}/like/{pattern}")
    public ResponseEntity<List<String>> getS3Files(@PathVariable String name, @PathVariable String pattern) {
        return ResponseEntity.ok(awsService.getS3Files(name, pattern));
    }
}
