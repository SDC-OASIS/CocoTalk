package com.cocotalk.user.repository;

import com.cocotalk.user.domain.entity.QUser;
import com.cocotalk.user.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    QUser quser = QUser.user;

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(jpaQueryFactory.select(quser)
                        .from(quser)
                        .where(quser.id.eq(id))
                        .fetchOne());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByCid(String Cid) {
        return Optional.empty();
    }
}
