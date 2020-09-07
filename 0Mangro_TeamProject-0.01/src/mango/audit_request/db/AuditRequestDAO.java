package mango.audit_request.db;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import mango.connection.db.DBconnection;

public class AuditRequestDAO extends DBconnection implements IAuditRequest{

	@Override
	public List<AuditRequestBean> getAuditList(AuditRequestBean aab) {		
		List<AuditRequestBean> list = new ArrayList<AuditRequestBean>();
		try {
			getConnection();
			String sql = "select * from audit_request "
					+ "where mem_email=?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, aab.getMemEmail());
			
			rs = pstmt.executeQuery();		
			
			AuditRequestBean bean = null;			
			
			while(rs.next()){
//				System.out.println("localdate6 : " + rs.getDate(6).toLocalDate());
//				System.out.println("localdate7 : " + rs.getDate(7).toLocalDate());
//				System.out.println("localdate8 : " + rs.getDate(8).toLocalDate());
				
				java.time.LocalDate confirmDate = null;				
				if(rs.getDate(8) != null)
					confirmDate = rs.getDate(8).toLocalDate();
				else
					confirmDate = null;
				
				bean = new AuditRequestBean(
						rs.getInt(1),
						rs.getString(2),
						rs.getInt(3),
						rs.getString(4),
						rs.getString(5),
						rs.getDate(6).toLocalDate(),
						rs.getDate(7).toLocalDate(),						
						confirmDate
						
						);
				list.add(bean);
			}
		
		} catch (Exception e) {			
			e.printStackTrace();
		} finally{
			resourceClose();
		}	
		return list;	
	}

	@Override
	public List<AuditRequestBean> getAllAuditList() {
		List<AuditRequestBean> list = new ArrayList<AuditRequestBean>();
		try {
			getConnection();
			String sql = "select * from audit_request";
			
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();		
			
			AuditRequestBean bean = null;			
			
			while(rs.next()){
				
				java.time.LocalDate confirmDate = null;				
				if(rs.getDate(8) != null)
					confirmDate = rs.getDate(8).toLocalDate();
				else
					confirmDate = null;
				
				bean = new AuditRequestBean(
						rs.getInt(1),
						rs.getString(2),
						rs.getInt(3),
						rs.getString(4),
						rs.getString(5),
						rs.getDate(6).toLocalDate(),
						rs.getDate(7).toLocalDate(),						
						confirmDate
						
						);
				list.add(bean);
			}
		
		} catch (Exception e) {			
			e.printStackTrace();
		} finally{
			resourceClose();
		}	
		return list;	
	}	
	
	

	@Override
	public List<AuditRequestBean> getAllAuditListApproved(int aca_main_num) {
		List<AuditRequestBean> list = new ArrayList<AuditRequestBean>();
		try {
			getConnection();
			String sql = "select * "
					+ "from audit_request "
					+ "where aca_main_num = ? "
					+ "and audit_confirm_date is not null";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, aca_main_num);
			
			rs = pstmt.executeQuery();		
			
			AuditRequestBean bean = null;			
			
			while(rs.next()){
				
				java.time.LocalDate confirmDate = null;				
				if(rs.getDate(8) != null)
					confirmDate = rs.getDate(8).toLocalDate();
				else
					confirmDate = null;
				
				bean = new AuditRequestBean(
						rs.getInt(1),
						rs.getString(2),
						rs.getInt(3),
						rs.getString(4),
						rs.getString(5),
						rs.getDate(6).toLocalDate(),
						rs.getDate(7).toLocalDate(),						
						confirmDate
						
						);
				list.add(bean);
			}
		
		} catch (Exception e) {			
			e.printStackTrace();
		} finally{
			resourceClose();
		}	
		return list;	
	}

	@Override
	public int InsertAudit(AuditRequestBean insert) {
		int result = 0;
		try {
			getConnection();
			String sql = "insert into audit_request "				
					+ " values(null, ?, ?, ?, ?, ?, ?, ?)";
			
			pstmt = con.prepareStatement(sql);	
			
			System.out.println(insert.toString());
			
			//1번 컬럼  audit_num은 auto-increment 설정되어있음
			pstmt.setString(1, insert.getMemEmail());
			pstmt.setInt(2, insert.getAcaNum());
			pstmt.setString(3, insert.getAcaName());
			pstmt.setString(4, insert.getAuditSubject());
			pstmt.setDate(5, java.sql.Date.valueOf(insert.getAuditWishDate()));
			pstmt.setDate(6, java.sql.Date.valueOf(insert.getAuditRequestDate()));		
			pstmt.setDate(7, null);		//가입승인날짜는 최초에 null
			
			result = pstmt.executeUpdate();
			
		} catch (Exception e) {
			System.out.println("InsertAudit()에서 예외 발생");
			e.printStackTrace();
		} finally{
			resourceClose();
		}		
		return result;
	}
	

	@Override
	public int CheckAuditList(AuditRequestBean check) {
		
		//★★★기본값 : 1★★★ 
		//중복 레코드셋이 존재하지 않는다면 청강신청이 가능하므로 1 반환
		int result = 1;
		
		try {
			getConnection();
			String sql = "select audit_wish_subject from audit_request "
					+ "where mem_email = ?";		
			
			pstmt = con.prepareStatement(sql);			
			pstmt.setString(1, check.getMemEmail());			
			rs = pstmt.executeQuery();					
			
			while(rs.next()){
				//한 사람 당 과목 중복 신청 불가
				//form에서 선택한 과목이 DB에 이미 등록되어 있으면 청강신청 불가 
				if(check.getAuditSubject().equals(rs.getString(1))){							
					result = 0;
					break;							
				}else{
					//DB에 없으면 중복X -> 청강신청 가능
					result = 1;	
				}					
			}					
			
		} catch (Exception e) {
			System.out.println("CheckAuditList()에서 예외 발생");
			e.printStackTrace();
		} finally{
			resourceClose();
		}		
		return result;
	}

	@Override
	public int ApprovalAudit(AuditRequestBean app, String check) {
		
		int result = 0;
		try {
			getConnection();
			
			System.out.println("check : " + check);
			System.out.println("getAuditNum : " + app.getAuditNum());
			System.out.println("getAcaNum : " + app.getAcaNum());
			
			String sql = null;
			if(check.equals("approve")){
				sql = "update audit_request "
						+ "set audit_confirm_date = curdate() "
						+ "where audit_num = ? and aca_main_num = ?";
				
			}else if(check.equals("reject")){
				sql = "update audit_request "
						+ "set audit_confirm_date = null "
						+ "where audit_num = ? and aca_main_num = ?";
				
			}else if(check.equals("delete")){
				sql = "delete from audit_request "					
						+ "where audit_num = ? and aca_main_num = ?";
			}			
			
			pstmt = con.prepareStatement(sql);	
			pstmt.setInt(1, app.getAuditNum());
			pstmt.setInt(2, app.getAcaNum());
			
			result = pstmt.executeUpdate();				
			
		} catch (Exception e) {
			System.out.println("ApprovalAudit()에서 예외 발생");
			e.printStackTrace();
		} finally{
			resourceClose();
		}		
		return result;
	}
	
	@Override
	public int DeleteAudit(AuditRequestBean delete) {
		int result = 0;
		try {
			getConnection();
			String sql = "delete from audit_request "					
					+ "where audit_num = ? and aca_main_num = ?";
			
			pstmt = con.prepareStatement(sql);	
			pstmt.setInt(1, delete.getAuditNum());
			pstmt.setInt(2, delete.getAcaNum());
			
			result = pstmt.executeUpdate();
			
			System.out.println("getAuditNum : " + delete.getAuditNum());
			System.out.println("getAcaNum : " + delete.getAcaNum());
			
		} catch (Exception e) {
			System.out.println("DeleteAudit()에서 예외 발생");
			e.printStackTrace();
		} finally{
			resourceClose();
		}		
		return result;
	}

	@Override
	public int getAuditCount() {
		int count = 0;
		try {
			getConnection();
			String sql = "select count(*) from audit_request";			
			pstmt = con.prepareStatement(sql);	
			rs = pstmt.executeQuery();
			
			rs.next();
			
			//누적청강수
			count = rs.getInt(1);			
			
		} catch (Exception e) {
			System.out.println("getAuditCount()에서 예외 발생");
			e.printStackTrace();
		} finally{
			resourceClose();
		}		
		return count;
	}

	@Override
	public int getAuditCountApproval() {
		int count = 0;
		try {
			getConnection();
			String sql = "select count(*) from audit_request "
					+ "where audit_confirm_date is not null";			
			pstmt = con.prepareStatement(sql);	
			rs = pstmt.executeQuery();
			
			rs.next();			
			
			count = rs.getInt(1);			
			
		} catch (Exception e) {
			System.out.println("getAuditCountApproval()에서 예외 발생");
			e.printStackTrace();
		} finally{
			resourceClose();
		}		
		return count;
	}

	@Override
	public int getAuditCountOfSubject(String subject) {
		int count = 0;
		try {
			getConnection();
			String sql = "select count(*) from audit_request where audit_wish_subject=?";			
			pstmt = con.prepareStatement(sql);	
			pstmt.setString(1, subject);
			rs = pstmt.executeQuery();
			
			rs.next();			
			
			count = rs.getInt(1);			
			
		} catch (Exception e) {
			System.out.println("getAuditCountOfSubject()에서 예외 발생");
			e.printStackTrace();
		} finally{
			resourceClose();
		}		
		return count;
	}
	
	
	
	

}
