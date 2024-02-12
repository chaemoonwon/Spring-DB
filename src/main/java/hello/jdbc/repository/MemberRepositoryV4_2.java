package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/*
* 예외 누수 문제 해결
* 체크 예외를 런타임 예외로 변경
* MemberRepository 인터페이스 사용
* throws SQLException 제거
* */

@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository{

    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //JDBC 개발 - 등록
    @Override
    public Member save(Member member) {
        //prepareStatement를 이용한 파라미터 바인딩 방식을 이용해서 쿼리를 짜야함
        String sql = "insert into member(member_id, money) values(? , ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();  //데이터를 변경할 때 사용
            return member;
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            //리소스 정리는 항상 수행되어야 함
            close(con, pstmt, null);
//            pstmt.close();  //Exception이 발생할 경우 con.close호출 되지 않음
//            con.close();
        }
    }

    //JDBC 개발 - 조회
    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();  //데이터를 조회할때 사용
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId= " + memberId);
            }
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(con, pstmt, rs);
        }

    }


    //JDBC 개발 - 수정
    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate(); //수정한 row수를 반환
            log.info("resultSize={}", resultSize);

        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            JdbcUtils.closeStatement(pstmt);
        }
    }

    //JDBC 개발 - 삭제
    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_Id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        //해제할 때 순서
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        // 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() throws SQLException {
        // 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        Connection con = DataSourceUtils.getConnection(dataSource);
//        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
