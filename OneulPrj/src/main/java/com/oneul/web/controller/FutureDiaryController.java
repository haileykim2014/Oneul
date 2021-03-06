package com.oneul.web.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.oneul.web.entity.CalendarEmotion;
import com.oneul.web.entity.FutureDiary;
import com.oneul.web.entity.FutureDiaryComment;
import com.oneul.web.entity.Member;
import com.oneul.web.service.CalendarEmotionService;
import com.oneul.web.service.FutureDiaryCommentService;
import com.oneul.web.service.FutureDiaryService;
import com.oneul.web.service.MemberService;

@EnableScheduling
@Controller
@RequestMapping("/diary/futureDiary/")
public class FutureDiaryController {

	@Autowired
	private FutureDiaryService service;
	
	@Autowired
	private FutureDiaryCommentService commentService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private CalendarEmotionService calendarService;

	
	@RequestMapping("list")
	public String list(Model model) {
		List<FutureDiary> list = service.getList(1);
		
		model.addAttribute("list", list);
		return("diary/futureDiary/list");
	}
	
	@RequestMapping("detail")
	public String detail(Model model, int id) {
		FutureDiary futureDiary = service.get(id);
		
		model.addAttribute("futureDiary", futureDiary);
		
		List<FutureDiaryComment> list = commentService.getViewList(id);
		model.addAttribute("commentList", list);
		return("diary/futureDiary/detail");
	}
	
	@PostMapping("commentReg")
	public String commentReg(int id, String content) {
		FutureDiaryComment comment = new FutureDiaryComment();
		comment.setContent(content);
		comment.setFutureDiaryId(id);
		comment.setMemberId(6);
		
		commentService.insert(comment);
		return("redirect:detail?id="+id);
	}
	
	@RequestMapping("commentDel")
	public String commentDel(int id, int futureDiaryId) {
		commentService.delete(id);
		
		return("redirect:detail?id="+futureDiaryId);
		
	}
	
	@PostMapping("reg")
	public String reg(@DateTimeFormat(pattern = "yyyy-MM-dd")Date bookingDate, 
					  String content,
					  MultipartFile file,
					  
					  String emotionId,
					  HttpServletRequest request) {
		
		int emt = Integer.parseInt(emotionId);
		
		String fileName = file.getOriginalFilename();
		
		HttpSession session = request.getSession(true);//????????? ??????????????? ????????????->??????????????????????????????
		String username = (String) session.getAttribute("username");
		
		Member member = new Member();
		member = memberService.get(username);
		int memberId = member.getId();
		
		FutureDiary futureDiary = new FutureDiary();	
		futureDiary.setBookingDate(bookingDate);
		futureDiary.setContent(content);
		futureDiary.setMemberId(memberId);
		
		futureDiary.setEmotionId(emt);
		futureDiary.setImage(fileName);
		
		service.insert(futureDiary);
		

		System.out.println(futureDiary.getId());
		int id = futureDiary.getId();
		
		if(!fileName.equals("")) {
			ServletContext application = request.getServletContext();
			String path = "/upload/diary/futureDiary/"+memberId+"/"+id; //??????id + ??????id..
			String realPath = application.getRealPath(path);
			
			File pathFile = new File(realPath);
			if(!pathFile.exists())
				pathFile.mkdirs();
			
			String filePath = realPath + File.separator +fileName; 
			
			System.out.println(filePath);
			File saveFile = new File(filePath);
			
			try {
				file.transferTo(saveFile);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		
		
		return("redirect:list");
	}
	
	@GetMapping("reg")
	public String reg() {
		return("diary/futureDiary/reg");
	}
	
	@GetMapping("edit")
	public String edit(int id,Model model) {
		FutureDiary futureDiary = service.get(id);
		
		model.addAttribute("futureDiary", futureDiary);
		return("diary/futureDiary/edit");
	}
	
	@PostMapping("edit")
	public String edit(FutureDiary futureDiary,
						MultipartFile file, 
						HttpServletRequest request,
						int changed,
						String originalFile,
						CalendarEmotion calendarEmotion) {
		System.out.println(changed);
		System.out.println(originalFile);
		
		int id = futureDiary.getId();
 
		
		HttpSession session = request.getSession(true);//????????? ??????????????? ????????????->??????????????????????????????
		String username = (String) session.getAttribute("username");
		
		Member member = new Member();
		member = memberService.get(username);
		int memberId = member.getId();
	
		
		//?????? ??????????????? ?????? ?????? ????????? ??????
		if(changed ==0) {
			futureDiary.setImage(originalFile);
		}
		
		String fileName = file.getOriginalFilename();
		
		// ?????? ???????????? ??????
		//?????? ?????? ????????? ????????? ????????? ??????
		if(!fileName.equals("") && changed == 1) {
			
			ServletContext application = request.getServletContext();
			//?????? ?????? ??????
	         String prevFilePath = "/upload/diary/futureDiary/"+memberId+"/"+id;
	         String prevFilerealPath = application.getRealPath(prevFilePath);
	         String deleteFilePath = prevFilerealPath + File.separator+originalFile;
	         System.out.println(deleteFilePath);
	         
	         File deleteFile = new File(deleteFilePath);
	          if(deleteFile.exists()) {	                  
	                  // ?????? ??????.
	             deleteFile.delete(); 
	             System.out.println("????????????");
	          }

			String path = "/upload/diary/futureDiary/"+memberId+"/"+id;
			String realPath = application.getRealPath(path);
			
			File pathFile = new File(realPath);
			if(!pathFile.exists())
				pathFile.mkdirs();
			
			String filePath = realPath + File.separator + fileName;
			
			//System.out.println(filePath);
			File saveFile = new File(filePath);
			
			try {
				file.transferTo(saveFile);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			futureDiary.setImage(fileName);
		}
		
		//?????? ???????????? ??????
		if(fileName.equals("")&& changed == 1) {
			
			ServletContext application = request.getServletContext();
	
	         String prevFilePath = "/upload/diary/futureDiary/"+memberId+"/"+id;
	         String prevFilerealPath = application.getRealPath(prevFilePath);
	         
	         File folder = new File(prevFilerealPath);
	         while(folder.exists()) {
	     		File[] folder_list = folder.listFiles(); //??????????????? ????????????
	     				
	     		for (int j = 0; j < folder_list.length; j++) {
	     			folder_list[j].delete(); //?????? ?????? 
	     			System.out.println("????????? ?????????????????????.");
	     					
	     		}
	     				
	     		if(folder_list.length == 0 && folder.isDirectory()){ 
	     			folder.delete(); //???????????? ??????
	     			System.out.println("????????? ?????????????????????.");
	     		}
	                 }
	        
	         
			futureDiary.setImage(fileName);			
		}
		
		service.update(futureDiary);
		return "redirect:detail?id="+futureDiary.getId();
	}
	
	@RequestMapping("del")
	public String del(int id,HttpServletRequest request) {
		HttpSession session = request.getSession(true);//????????? ??????????????? ????????????->??????????????????????????????
		String username = (String) session.getAttribute("username");
		
		Member member = new Member();
		member = memberService.get(username);
		int memberId = member.getId();
	
		
		ServletContext application = request.getServletContext();
		
        String prevFilePath = "/upload/diary/futureDiary/"+memberId+"/"+id;
        String prevFilerealPath = application.getRealPath(prevFilePath);
        
        File folder = new File(prevFilerealPath);
        while(folder.exists()) {
    		File[] folder_list = folder.listFiles(); //??????????????? ????????????
    				
    		for (int j = 0; j < folder_list.length; j++) {
    			folder_list[j].delete(); //?????? ?????? 
    			System.out.println("????????? ?????????????????????.");
    					
    		}
    				
    		if(folder_list.length == 0 && folder.isDirectory()){ 
    			folder.delete(); //???????????? ??????
    			System.out.println("????????? ?????????????????????.");
    		}
                }
       
		
		service.delete(id);
		
		return("redirect:list");
	}
	
	
		//js?????? ???????????? onclick -> ?????????????????? src ?????????..
		//??????????????? ?????????????????? ????????????(????????? ????????? ''?????? ??????????????????..???????????????? ???????????? ????????????..
	
	//second, minute, hour, day, month, weekday
	
	//cron(0 10 * * ? *) 16??? (0 0 0 * * *)
	
	@Scheduled(cron="0 1 0 * * *")
	public void printHi() {
		
		List<FutureDiary> list = service.getListToday();
		
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//		Date time = new Date();
//		
//		String today = format.format(time); //????????????

//		for(FutureDiary fd : list) {
//			String bookingDate = format.format(fd.getBookingDate()); //????????????
//			if(today.equals(bookingDate)) {
//				int memberId = fd.getMemberId();
//				Member member = memberService.get(memberId);
//				String email = member.getEmail();
//				System.out.println(email);
//				String title = "??????????????? ??? ????????? ???????????????";
//				String body = "?????? ?????? ???????????????" + "http://localhost:8080/diary/futureDiary/detail?id="+fd.getId();
//				memberService.sendEmail(email,title,body);
//				System.out.println("????????????");
//			}
//			
//		}
		
		for(FutureDiary fd : list) {
			int memberId = fd.getMemberId();
			Member member = memberService.get(memberId);
			String email = member.getEmail();
			System.out.println(email);
			String title = "??????????????? ??? ????????? ???????????????";
			//String body = "?????? ?????? ???????????????\n" + "http://localhost:8080/diary/futureDiary/detail?id="+fd.getId();
			String body = "?????? ?????? ???????????????" + "http://oneul.gonetis.com:8080/diary/futureDiary/detail?id="+fd.getId();
			memberService.sendEmail(email,title,body);
			System.out.println("????????????");
		
		}
		
		
	}
	
}
