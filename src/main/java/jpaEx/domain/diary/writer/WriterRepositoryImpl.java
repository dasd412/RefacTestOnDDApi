package jpaEx.domain.diary.writer;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class WriterRepositoryImpl implements WriterRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    /*
    fetch : 조회 대상이 여러건일 경우. 컬렉션 반환
    fetchOne : 조회 대상이 1건일 경우(1건 이상일 경우 에러). generic 에 지정한 타입으로 반환
    fetchFirst : 조회 대상이 1건이든 1건 이상이든 무조건 1건만 반환. 내부에 보면 return limit(1).fetchOne() 으로 되어있음
    fetchCount : 개수 조회. long 타입 반환
    fetchResults : 조회한 리스트 + 전체 개수를 포함한 QueryResults 반환. count 쿼리가 추가로 실행된다.
     */

    public WriterRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Long findCountOfId() {
        return jpaQueryFactory.from(QWriter.writer).fetchCount();
    }

    @Override
    public Long findMaxOfId() {
        return jpaQueryFactory.from(QWriter.writer).select(QWriter.writer.writerId.max()).fetchOne();
    }
}
