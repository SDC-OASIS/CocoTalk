package com.cocotalk.user.repository;

import com.cocotalk.user.domain.entity.QUser;
import com.cocotalk.user.domain.entity.User;
import com.querydsl.core.BooleanBuilder;
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
    public Optional<User> find(String cid, String email, String phone) {
        return Optional.ofNullable(jpaQueryFactory.select(quser)
                .from(quser)
                .where(cidOrEmailOrPhoneEq(cid, email, phone))
                .fetchOne());
    }

    private BooleanBuilder cidOrEmailOrPhoneEq(String cid, String email, String phone) {
        return cidEq(cid).or(emailEq(email)).or(phoneEq(phone));
    }

    private BooleanBuilder cidEq(String cid) {
        // nullSafeBuilder(() -> quser.cid.eq(cid));
        return cid == null ? new BooleanBuilder() : new BooleanBuilder(quser.cid.eq(cid));
    }

    private BooleanBuilder emailEq(String email) {
        return email == null ? new BooleanBuilder() : new BooleanBuilder(quser.email.eq(email));
    }

    private BooleanBuilder phoneEq(String phone) {
        return phone == null ? new BooleanBuilder() : new BooleanBuilder(quser.phone.eq(phone));
    }

//    public static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
//        try {
//            return new BooleanBuilder(f.get());
//        } catch (IllegalArgumentException e) {
//            return new BooleanBuilder();
//        }
//    }
}
