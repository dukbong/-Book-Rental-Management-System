package book.rental.management.repository.member.mybatis;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;

@RequiredArgsConstructor
public class MemberMybatisRepositoryImpl implements MemberMybatisRepository{

    private final SqlSession sqlSession;

    private static final String NAMESPACE = "book.rental.management.repository.book.mybatis.MemberMybatisRepositoryImpl.";

}
