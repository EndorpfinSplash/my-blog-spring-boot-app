package by.jdeveloper.myblogbackspringbootapp.repository;


import by.jdeveloper.myblogbackspringbootapp.dao.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@AllArgsConstructor
public class JdbcNativeFileRepository implements FileRepository {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public void saveFile(Long postId, String fileName, byte[] data) {
        jdbcTemplate.update(
                "insert into image (post_id, file_name, data) values (?, ?, ?)",
                postId, fileName, data);
    }

    public boolean updateFileByPostId(Long postId, String fileName, byte[] data) {
        try {
            jdbcTemplate.update(
                    "update image set file_name= ?, data = ? where post_id= ?",
                    fileName, data, postId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] getFileByPostId(Long postId) {
        String sql = "SELECT data FROM image WHERE post_id = ?";
        return jdbcTemplate.queryForObject(sql, byte[].class, postId);
    }

    @Override
    public Long countFilesByPostId(Long postId) {
        String sql = "SELECT count(*) FROM image WHERE post_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, postId);
    }

}