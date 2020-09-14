package mango.academy_register.action;

import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import mango.academy_register.db.AcademyRegisterBean;
import mango.academy_register.db.AcademyRegisterDAO;
import mango.action.Action;
import mango.action.ActionForward;
import mango.member.db.MemberDAO;

public class AcademyRegisterAction implements Action{

	@Override
	public ActionForward excute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		request.setCharacterEncoding("utf-8");
		
		String email = (String)request.getSession().getAttribute("id_email");
		
		//---------------로그인이 되어있는지 확인-------------------------//
		if(email == null || email.equals("")){
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();			
			out.println("<script>");
			out.println("alert('로그인이 필요합니다!');");
			out.println("history.back();");
			out.println("</script>");
			out.close();
			return null;
		}
		
		//--------------DB에 학원관리자정보가 등록되어있는지 확인---------------------//
		AcademyRegisterDAO dao = new AcademyRegisterDAO();		
		int exists = dao.CheckDataExists(email);
		//DB에 데이터가 존재할 경우 등록 진행 불가
		if(exists == 1){
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();			
			out.println("<script>");
			out.println("alert('DB에 등록된 정보가 있습니다.');");
			out.println("history.back();");
			out.println("</script>");
			out.close();
			return null;
		}	
		
//==============================DB에 등록된 데이터가 없을경우 등록 진행=================================
		
		//==============MultipartRequest 객체로 form태그 데이터 받아오기===============================
		//업로드할 실제 서버의 경로 얻기
		String realFolder = request.getServletContext().getRealPath("/O_aca_regFiles/upload/images/");
		int max = 10 * 1024 * 1024; //업로드용량, 10MB
		
		MultipartRequest multi =
		 		new MultipartRequest(request, 
		 							 realFolder, // 파일을 저장할 디렉토리 지정
		 							 max,  // 첨부파일 최대 용량 설정(byte) / 10KB / 용량 초과 시 예외 발생
		 							 "UTF-8", // 인코딩 방식 지정
		 							 new DefaultFileRenamePolicy()); // 중복 파일 처리(동일한 파일명이 업로드되면 뒤에 숫자 등을 붙여 중복 회피)

		//=============파일태그의 데이터로부터 DB에 업로드할 이미지 파일명, 크기 받아오기(사업자 등록증,대표자 신분증/재직증명서)===============================================		
		
		// academy_register.jsp에서 파일선택 또는 드래그 시 파일태그에 데이터가 입력되고
		// fNameCompany, fNameOwner라는 이름의 input태그에 파일명이 입력되게 해놨음
		
		// 파일태그에서 추출한 파일명
		String fNameCompany = multi.getParameter("fNameCompany");	
		String fNameOwner = multi.getParameter("fNameOwner");	
		
		// 실제로 DB에 들어갈 파일경로
		String[] realPath_Arr = new String[2];
		
		Enumeration e = multi.getFileNames();
		int idx = 0;
		while(e.hasMoreElements()){				
			 		
			// MultipartRequest가 파일태그명(fileName)을 통해 form에서 전송된 file태그에 접근함
			// 파일태그명
	 		String fileName = (String)e.nextElement();	 	 		
	 		
	 		//클라이언트가 업로드한 파일의 원본이름
	 		String originalFileName = multi.getOriginalFileName(fileName);	 	
	 		//서버에 업로드할 파일명
	 		String fileSystemName = multi.getFilesystemName(fileName);	 	
	 		//이미지 파일 폴더 경로
	 		String folderPath = "O_aca_regFiles/upload/images/";	
	 		
	 		//	업로드할 파일명 + 이미지폴더 경로를 합해서 DB에 삽입
	 		realPath_Arr[idx] = folderPath + fileSystemName;
	 		
	 		System.out.println("originalFileName : " + originalFileName); 
	 		System.out.println("realPath_Arr : " + realPath_Arr[idx]); 	
	 		
	 		
	 		//배열 인덱스 증가
			idx++;
		
		}			

		
		//=============academy_register 테이블에 등록==========================
		String addr_zip = multi.getParameter("addr_zip");
		String addr_doro = multi.getParameter("addr_doro");
		String acaName = multi.getParameter("acaName");		
		String keyword = multi.getParameter("keyword");		
		String f_size_company = multi.getParameter("f_size_company");			
		String f_size_owner = multi.getParameter("f_size_owner");			
		
		AcademyRegisterBean bean = new AcademyRegisterBean();
		bean.setMemEmail(email);
		bean.setMemAddrZip(addr_zip);
		bean.setMemAddrDoro(addr_doro);
		bean.setAcaName(acaName);		
		bean.setAca_keyword(keyword);
		
		bean.setfNameCompany(realPath_Arr[0]);
		bean.setfNameOwner(realPath_Arr[1]);		
		
		bean.setfSizeCompany(f_size_company);
		bean.setfSizeOwner(f_size_owner);		
		
		int result = dao.RegisterToDB(bean);		
		if(result == 0){
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('failed');");
			out.println("history.back();");
			out.println("</script>");
			out.close();
			return null;
		}else{
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('success');");
			out.println("location.href='./4index.jsp'");
			out.println("</script>");
			out.close();
			return null;
		}	
		

	}//execute()

	
}//AcademyRegisterAction
