package mango.academy_review.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import mango.academy_review.db.AcademyReviewDAO;

// 후기많은 Top3 메인에 출력
@WebServlet("/reviewTop")
public class ReviewTopAction extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doProcess(req,resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doProcess(req,resp);
	}
	
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		String data = request.getParameter("data");
		
		PrintWriter out = response.getWriter();
		
		try {
			
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(data);
			

			AcademyReviewDAO dao = new AcademyReviewDAO();
			
			int rank = Integer.parseInt((String)obj.get("num"))-1;
			System.out.println(rank);
			// 학원이름
			String acaName = dao.getAcaNameTop(rank);
			System.out.println(acaName);
			// 후기갯수
			int revCnt = dao.getReviewCntTop(rank);
			System.out.println("후기갯수:"+revCnt);
			// 평균평점
			double avgScore = 
					Double.parseDouble(String.format("%.1f",dao.getAvgScoreTop(rank)));
			System.out.println("이까지4");
			System.out.println(avgScore);
			List<String> titleList = dao.getReviewTitle(acaName);
			System.out.println(titleList.get(0));
			System.out.println(titleList.get(1));
			
			obj.put("acaName", acaName);
			obj.put("revCnt", revCnt);
			obj.put("avgScore", avgScore);
			obj.put("title1", titleList.get(0));
			obj.put("title2", titleList.get(1));
			
			
			out.print(obj);
			
		} catch (Exception e) {
			System.out.println("ReviewTopAction()에서 예외 발생!");
			e.printStackTrace();
		}
		
		
		
		
		
	}

	
	
}