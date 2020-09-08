package mango.member.db;
import java.util.ArrayList;
import java.util.List;

import mango.connection.db.DBconnection;
import mango.payment.db.PaymentBean;
public class MemberDAO extends DBconnection{
	
	/* 일반 회원 가입  메서드 */
	public boolean insertMember(MemberBean mb){
		
		int result = 0;
		
		try {
			getConnection();
			System.out.println("DB 연결 성공 !!");
			
			sql = "INSERT INTO member (mem_email, mem_name, mem_pwd, mem_joindate)"
					+ " VALUES (?,?,?, now())";
			
			// [참고] pm_use_num : 사용회자 / 무료 체험도 1번 횟수로 적용
			//       pm_name : 이용권 이름 / 가입 무제한 이용권 (3일)

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, mb.getMemEmail());
			pstmt.setString(2, mb.getMemName());
			pstmt.setString(3, mb.getMemPwd());
			
			result = pstmt.executeUpdate();
			
			if(result != 0){
				
				getConnection();
				
				sql = "INSERT INTO payment (mem_email, pm_use_num, pm_name, pm_start_date, pm_exp_date) "
					+ " VALUES (?, 1,'가입 무제한 이용권 (3일)',now(), DATE_ADD(NOW(), INTERVAL 3 DAY))";
				
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, mb.getMemEmail());
				pstmt.executeUpdate();

				return true;
			}
			
			System.out.println("회원 가입 완료 !!");
			
		} catch (Exception e) {
			System.out.println("--> insertMember()에 SQL구문 오류" + e);
			e.printStackTrace();
		} finally { // 자원 해제
			resourceClose();
		} // try문 끝
		
		return false;
	} // 일반 회원 가입 / insertMember() 끝
	
	
	
	
	/* 네이버 회원 가입  메서드 */
	public boolean insertnaverMember(MemberBean mb){
		
		int result = 0;
		
		try {
			getConnection();
			System.out.println("DB 연결 성공 !!");
			
			sql = "INSERT INTO member (mem_email, mem_name, "
					+ "mem_joindate)"
					+ " VALUES (?,?, now())";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, mb.getMemEmail());
			pstmt.setString(2, mb.getMemName());
			
			System.out.println(mb);
			
			result = pstmt.executeUpdate();
			
			if(result != 0){
				return true;
			}
			
			System.out.println("회원 가입 완료 !!");
			
		} catch (Exception e) {
			System.out.println("--> insertMember()에 SQL구문 오류" + e);
			e.printStackTrace();
		} finally { // 자원 해제
			resourceClose();
		} // try문 끝
		
		return false;
	} // 네이버 회원 가입 / insertnaverMember() 끝
	
	
	
	/* 일반 로그인 메서드 */
	public int loginCheck(MemberBean mb){
		
		int check = 0;
		
		try {
			getConnection();
			
			sql = "SELECT * FROM member WHERE mem_email = ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, mb.getMemEmail());
			rs = pstmt.executeQuery();
			
			if(rs.next()){ // SELECT 결과에 아이디가 있을 때
				
				// 비밀번호가 일치할 때
				if(rs.getString("mem_pwd").equals(mb.getMemPwd())){
						
					// 탈퇴일자 컬럼과 정지일자 컬럼에 데이터가 존재할 때 로그인 불가
					if(!(rs.getString("mem_seceded") == null) 
					|| !(rs.getString("mem_baned") == null) ){ 
						check = -2;
						
					// 탈퇴일자 컬럼과 정지일자 컬럼에 데이터가 null일 때 로그인 성공
					}else if((rs.getString("mem_seceded") == null) 
						  && (rs.getString("mem_baned") == null)){
						check = 1;
					}
				
				// 비밀벝호가 일치하지 않을 때 (0)
				}else if(!(rs.getString("mem_pwd").equals(mb.getMemPwd())) ){
					check = 0;
				}
				
			}else{
				check = -1; // SELECT 결과에 아이디 없을 때 (-1)
			}
			System.out.println("DB 조회 성공 !!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("--> loginCheck()에서 SQL구문 오류 : " + e);
		} finally { // 자원 해제
			resourceClose();
		} // try문 끝
		return check;
	} // 일반 로그인 / loginCheck() 끝
	
	
	
	
	/* 네이버 로그인 메서드 */
	public int naverloginCheck(MemberBean mb){
			
		int check = 0;
		
		try {
			getConnection();
			
			sql = "SELECT * FROM member WHERE mem_email = ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, mb.getMemEmail());
			rs = pstmt.executeQuery();
			
			if(rs.next()){ // SELECT 결과에 아이디가 있을 때
				
				if(rs.getString("mem_email").equals(mb.getMemPwd()) 
				&& rs.getString("mem_name").equals(mb.getMemName()) ){
						
					// 탈퇴일자 컬럼과 정지일자 컬럼에 데이터가 존재할 때 로그인 불가
					if(!(rs.getString("mem_seceded") == null)
					|| !(rs.getString("mem_baned") == null)){ 
						check = -2;
						
						// 탈퇴일자 컬럼과 정지일자 컬럼에 데이터가 null일 때 로그인 성공
					}else if((rs.getString("mem_seceded") == null) 
						  && (rs.getString("mem_baned") == null)){
						check = 1;
					}
				}	
				
			}else{
				check = -1; // SELECT 결과에 아이디 없을 때 (-1)
			}
			System.out.println("DB 조회 성공 !!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("--> naverloginCheck()에서 SQL구문 오류 : " + e);
		} finally { // 자원 해제
			resourceClose();
		} // try문 끝
		return check;
	} // 네이버 로그인 / naverloginCheck() 끝
		
	
	
	
	/* 회원 탈퇴 메서드 */
	public int deleteMember(MemberBean mb){
		
		int check = 0;
		
		try {
			getConnection();
			sql = "SELECT * FROM member WHERE mem_email = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, mb.getMemEmail());
			rs = pstmt.executeQuery();
			if(rs.next()){
				
				if(rs.getString("mem_pwd").equals(mb.getMemPwd())){
					check = 1;
					
					sql = "UPDATE member "
						+ "SET mem_seceded = now() "
						+ "WHERE mem_email = ?";
					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, mb.getMemEmail());
					pstmt.executeUpdate();
				}else{
					check = 0;
				}
				
			}else{
				check = 0;
			}
			
			System.out.println("회원 탈퇴 완료 !!");
			
		} catch (Exception e) {
			System.out.println("--> deleteMember()에서 SQL 구문 오류 : "+ e);
			e.printStackTrace();
		} finally {
			resourceClose();
		}
		return check;
	} // 회원 탈퇴 / deleteMember() 끝
	
	
	
	/* 내 정보 페이지에서 회원 정보가 보이는 기능의 메서드 */
	public String selectMember(String email) {
		String name = "";
		
		try {
			getConnection();
			
			sql = "SELECT mem_name "
				+ "FROM member "
				+ "WHERE mem_email = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			if(rs.next()){
				name = rs.getString("mem_name");
			}
			System.out.println("회원 이름 조회 완료 !!");
		} catch (Exception e) {
			System.out.println("--> selectMember()에서 SQL 구문 오류 : "+ e);
			e.printStackTrace();
		} finally {
			resourceClose();
		}
		return name;
	} // 내 정보  / selectMember() 끝
	
	
	
	
	/* 회원 정보 수정 메서드 */
	public boolean updateMember(MemberBean mb, String newPw) {
		
		boolean result = false; 
		
		try {
			getConnection();
			System.out.println("DB 연결 성공 !!");
			
			sql = "SELECT * FROM member WHERE mem_email = ? AND mem_pwd = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, mb.getMemEmail());
			pstmt.setString(2, mb.getMemPwd());
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				sql = "UPDATE member SET mem_pwd = ? WHERE mem_email = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, newPw);
				pstmt.setString(2, mb.getMemEmail());
				pstmt.executeUpdate();
				
				result = true;
				System.out.println("회원 정보 수정 완료 !!");
			}else{
				result = false;
			}
			
		} catch (Exception e) {
			System.out.println("--> updateMember()에서 SQL구문 오류 : " + e);
			e.printStackTrace();
		} finally {
			resourceClose();
		}
		return result;
	} // 회원 정보 수정 / updateMember() 끝
	
	
	
	/* 비밀번호 찾기 기능 메서드 */
	public MemberBean findPw(String email) {
	
		MemberBean mb = new MemberBean();
		
		try {
			getConnection();
			
			sql = "SELECT mem_pwd FROM member WHERE mem_email = ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				mb.setMemPwd(rs.getString("mem_pwd"));
			}
			
			System.out.println("비밀번호 조회 완료 !!");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("--> findPw()에서 SQL구문 오류 : " + e);
		} finally {
			resourceClose();
		}
		return mb;
	} // 비밀번호 찾기  / findPw() 끝




	/* 모든 회원 조회 (회원 관리창) 기능 메서드  */
	public ArrayList ListAll(int startRow ,int pageSize) {

		ArrayList memberlist = new ArrayList();
		
		try {
			getConnection();
			
			sql = "SELECT * "
				+ "FROM member "
				+ "LIMIT ?, ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, pageSize);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				
				MemberBean mb = new MemberBean();
				mb.setMemEmail(rs.getString("mem_email"));
				mb.setMemName(rs.getString("mem_name"));
				mb.setMemAdmin(rs.getString("mem_admin"));
				mb.setMemJoindate(rs.getString("mem_joindate"));
				mb.setMemBaned(rs.getString("mem_baned"));
				mb.setMemSeceded(rs.getString("mem_seceded"));
			
				memberlist.add(mb);
			}

			System.out.println("전체 회원 조회 완료 !!");
			
		} catch (Exception e) {
			System.out.println("--> ListAll()에서 SQL구문 오류 :" + e);
			e.printStackTrace();
		} finally {
			resourceClose();
		}
		
		return memberlist;
	} // 모든 회원 조회 / ListAll() 끝
	
	
	
	
	/* 회원 관리 페이징 처리 기능 메서드 */
	public int getMGCount() {
		
		int count = 0;
		
		try {
			getConnection();

			sql = "SELECT COUNT(*) FROM member";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				count = rs.getInt(1);
			}
			
			System.out.println("회원 관리 페이징 처리 완료 !!");
		
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("--> getMGCount()에서 SQL구문 오류 : " + e);
		} finally {
			resourceClose();
		}
		return count;
	} // 회원 관리 페이징 / getMGCount() 끝
	
	
	//학원관리자 등록 시 admin값 변경하는 메서드
	public int changeAdmin(String email, int flag){
		int result = 0;		
		try {
			getConnection();
			
			//flag == 0이면 관리자 등급으로 변경 승인 취소
			if(flag == 0){
				sql = "update member "
						+ "set mem_admin = ? "
						+ "where mem_email = ?";			

				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, 0);
				pstmt.setString(2, email);	
				
			//관리자 등급으로 변경 승인
			}else{
				sql = "update member "
						+ "set mem_admin = ? "
						+ "where mem_email = ?";			

				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, 1);
				pstmt.setString(2, email);	
			}					
			
			result = pstmt.executeUpdate();	
			
		} catch (Exception e) {
			System.out.println("changeAdmin()에서 예외 발생" + e);
			e.printStackTrace();
		} finally {
			resourceClose();
		}		
		return result;		
	}//changeAdmin()
	
	public int updateProfileImg(String imgPath, String email){
		int result = 0;
		try {
			getConnection();
			
			sql = "update member "
					+ "set mem_profileImg = ? "
					+ "where mem_email = ?";			

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, imgPath);			
			pstmt.setString(2, email);
			
			result = pstmt.executeUpdate();	
			
		} catch (Exception e) {
			System.out.println("updateProfileImg()에서 예외 발생" + e);
			e.printStackTrace();
		} finally {
			resourceClose();
		}		
		return result;		
	}//updateProfileImg()
	
	public String getProfileImg(String email){
		String imgPath = null;
		try {
			getConnection();
			
			sql = "select mem_profileImg from member where mem_email = ?";	
			pstmt = con.prepareStatement(sql);			
			pstmt.setString(1, email);			
			rs = pstmt.executeQuery();	
			
			if(rs.next()){
				imgPath = rs.getString("mem_profileImg");
			}
			
		} catch (Exception e) {
			System.out.println("getProfileImg()에서 예외 발생" + e);
			e.printStackTrace();
		} finally {
			resourceClose();
		}		
		return imgPath;		
	}//getProfileImg()
	
	
	
	
	
	
	
}