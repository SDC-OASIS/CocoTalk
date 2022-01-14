package com.cocotalk.chat.repository;

import com.cocotalk.chat.document.Notice;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends ReactiveMongoRepository<Notice, String>, ReactiveQuerydslPredicateExecutor<Notice> {
}
