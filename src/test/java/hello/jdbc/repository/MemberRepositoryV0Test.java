package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {

        //save
        Member member = new Member("memberV100", 10000);
        repository.save(member);

        //findById
        Member findById = repository.findById(member.getMemberId());
        log.info("findMember = {}" + findById);
        log.info("findById == member {}", member == findById);  //인스턴스는 서로 다름
        log.info("findById equals member {}", member.equals(findById)); //값을 서로 비교
        assertThat(findById).isEqualTo(member);

        //update
        repository.update(member.getMemberId(), 20000);
        Member updateMember = repository.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());
        //삭제에 대한 검증
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);


    }
}