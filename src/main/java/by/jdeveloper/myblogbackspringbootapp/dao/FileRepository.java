package by.jdeveloper.myblogbackspringbootapp.dao;

public interface FileRepository {

    void saveFile(Long postId, String name, byte[] data);

    boolean updateFileByPostId(Long postId, String fileName, byte[] data);

    byte[] getFileByPostId(Long postId);

    Long countFilesByPostId(Long postId);
}