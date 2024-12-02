package book.rental.management.repository.member.querydsl;

import book.rental.management.domain.member.Member;
import book.rental.management.dto.MemberCondition;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface MemberQueryDslRepository {
    List<Member> getMemberByCondition(@NotNull MemberCondition condition);
}
