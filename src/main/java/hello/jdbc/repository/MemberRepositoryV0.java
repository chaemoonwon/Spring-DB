package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.lang.module.Configuration;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV0 {


    //JDBC 개발 - 등록
    public Member save(Member member) throws SQLException {
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
            log.error("db error", e);
            throw e;
        } finally {
            //리소스 정리는 항상 수행되어야 함
            close(con, pstmt, null);
//            pstmt.close();  //Exception이 발생할 경우 con.close호출 되지 않음
//            con.close();
        }
    }

    //JDBC 개발 - 조회
    public Member findById(String memberId) throws SQLException {
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
                throw new NoSuchElementException("member not found memberId= " +memberId);
            }
        } catch (SQLException e) {
            log.info("error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }

    }

    //JDBC 개발 - 수정
    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate(); //수정한 row수를 반환
            log.info("resultSize={}",resultSize);

        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    //JDBC 개발 - 삭제
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_Id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {

        //해제할 때 순서
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
