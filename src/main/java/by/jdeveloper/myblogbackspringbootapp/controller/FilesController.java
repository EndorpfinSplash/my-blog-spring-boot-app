package by.jdeveloper.myblogbackspringbootapp.controller;


import by.jdeveloper.myblogbackspringbootapp.service.FilesService;
import by.jdeveloper.myblogbackspringbootapp.service.PostService;
import jakarta.servlet.annotation.MultipartConfig;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@MultipartConfig
@RequestMapping("/api/posts")
@AllArgsConstructor
public class FilesController {

    private final FilesService filesService;
    private final PostService postService;

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> downloadFile(@PathVariable(name = "id") Long postId) {
        byte[] file = filesService.downloadFile(postId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(file);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable(name = "id") Long postId,
                                              @RequestParam("image") MultipartFile image) throws IOException {

        if (postService.findById(postId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Post not found");
        }
        if (image.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("empty file");
        }
        boolean ok = filesService.uploadImage(postId, image);
        if (!ok) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("failed to update image");
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("ok");
    }
}
