package by.jdeveloper.myblogbackspringbootapp.service;


import by.jdeveloper.myblogbackspringbootapp.dao.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@AllArgsConstructor
public class FilesService {

    private final FileRepository fileRepository;

    public boolean uploadImage(Long postId, MultipartFile image) throws IOException {
        String imageName = image.getOriginalFilename();
        if (fileRepository.countFilesByPostId(postId).equals(0L)) {
            fileRepository.saveFile(postId, imageName, image.getBytes());
        }
        return fileRepository.updateFileByPostId(postId, imageName, image.getBytes());
    }

    public byte[] downloadFile(Long postId) {
        if (fileRepository.countFilesByPostId(postId).equals(0L)) {
            return new byte[]{};
        }
        return fileRepository.getFileByPostId(postId);
    }

}
