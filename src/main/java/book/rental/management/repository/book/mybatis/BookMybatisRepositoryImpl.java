package book.rental.management.repository.book.mybatis;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;

@RequiredArgsConstructor
public class BookMybatisRepositoryImpl implements BookMybatisRepository {

    private final SqlSession sqlSession;

    private static final String NAMESPACE = "book.rental.management.repository.book.mybatis.BookMybatisRepositoryImpl.";

}
