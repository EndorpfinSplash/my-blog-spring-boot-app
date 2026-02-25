package by.jdeveloper.myblogbackspringbootapp.service;


import by.jdeveloper.myblogbackspringbootapp.dao.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@AllArgsConstructor
public class FilesService {

    private final PostRepository postRepository;

    public boolean uploadImage(Long postId, MultipartFile image) throws IOException {
        String imageName = image.getOriginalFilename();
        if (postRepository.countFilesByPostId(postId).equals(0L)) {
            postRepository.saveFile(postId, imageName, image.getBytes());
        }
        return postRepository.updateFileByPostId(postId, imageName, image.getBytes());
    }

    public byte[] downloadFile(Long postId) {
        if (postRepository.countFilesByPostId(postId).equals(0L)) {
            return new byte[]{};
        }
        return postRepository.getFileByPostId(postId);
    }

}
