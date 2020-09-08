package mango.member.action;
import java.io.PrintWriter;
import java.sql.Timestamp;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mango.action.Action;
import mango.action.ActionForward;
import mango.member.db.MemberBean;
import mango.member.db.MemberDAO;
public class MemberNaverJoinAction implements Action{
	@Override
	public ActionForward excute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		request.setCharacterEncoding("UTF-8");
		
		MemberBean mb = new MemberBean();
		MemberDAO mdao = new MemberDAO();
		
		mb.setMemEmail(request.getParameter("id_email"));
		mb.setMemName(request.getParameter("id_name"));
		
		boolean result = false;
		
		result = mdao.insertnaverMember(mb);
		
		if(result == false){
			response.setContentType("text/html; charset=UTF-8");
			
			PrintWriter out = response.getWriter(); // getWriter() 호출하면 PrintWriter객체 얻을 수 있음
			out.print("<script>");
			out.print("alert('회원가입 중 오류가 발생하였습니다.');");
			out.print("location.href='/4index.jsp'");
			out.print("</script>");
			
			return null;
		}else{
			response.setContentType("text/html; charset=UTF-8");
			
			PrintWriter out = response.getWriter(); // getWriter() 호출하면 PrintWriter객체 얻을 수 있음
			out.print("<script>");
			out.print("alert('회원가입이 완료되었습니다.');");
			out.print("location.href='./MemberLogin.me'");
			out.print("</script>");
//			return null;
		}
		
		ActionForward forward = new ActionForward();
		forward.setRedirect(true);
		forward.setPath("./MemberLogin.me");
		
		return forward;
	} //  excute() 끝
}