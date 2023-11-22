package struct.repository;

import struct.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query("select b from Url b where b.shortUrl = :shortUrl")
    Url findByName(@Param("shortUrl") String shortUrl);
}
