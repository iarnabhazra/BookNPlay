package com.booknplay.turf.files;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/turfs/files")
public class FileController {

    private final Path storage = Path.of("uploads");

    public FileController() throws IOException { Files.createDirectories(storage); }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String name = UUID.randomUUID() + (ext != null ? "." + ext : "");
        Path target = storage.resolve(name);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return ResponseEntity.ok(name);
    }

    @GetMapping("/{name}")
    public ResponseEntity<byte[]> download(@PathVariable String name) throws IOException {
        Path target = storage.resolve(name);
        if (!Files.exists(target)) return ResponseEntity.notFound().build();
        byte[] bytes = Files.readAllBytes(target);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }
}
