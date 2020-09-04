package com.dht.www.event.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dht.www.event.model.service.EventService;
import com.dht.www.exercise.model.vo.Compensation;
import com.dht.www.user.model.vo.Users;

@Controller
@RequestMapping("/event")
public class EventController {
	
	@Autowired
	private EventService eventService;

	//====================== 공통 함수 ======================
	
	//사용자 현재 포인트, 코인 조회
	//key - 'point', 'coin'
	private Map<String, Object> checkPC(Compensation com) {
		return eventService.checkPC(com);
	}
	
	//이벤트에서 얻은 포인트 추가 - Compensation inc 필드 이용
	//업데이트 성공한 행 수 반환
	private int insertPoint(Compensation com) {
		return eventService.insertPoint(com);
	}
	
	//코인 차감 : -1 (이벤트 1회 실행)
	//업데이트 성공한 행 수 반환
	private int insertCoin(Compensation com) {
		return eventService.insertCoin(com);
	}
	
	//=======================================================
	
	// 가위바위보 VIEW
	@RequestMapping(value="/rockpaper", method=RequestMethod.GET)
	public String eventRockPaperScissorsfinal() {
		return "event/rockpaper";
	}
	
	// 출석체크 VIEW
	@RequestMapping(value="/attendance", method=RequestMethod.GET)
	public void eventAttendancecheckfinal(Model model) {
		
		Calendar cal = Calendar.getInstance();
		
		cal.get(Calendar.YEAR);
		
		model.addAttribute("year", cal.get(Calendar.YEAR));
		model.addAttribute("month", cal.get(Calendar.MONTH));
	}
	
	@RequestMapping(value = "/attendance", method = RequestMethod.POST)
	@ResponseBody
	public void eventAttendancePoint(HttpSession session) {
		
		Users user = (Users) session.getAttribute("logInInfo");
		System.out.println("잘 넘어오나"+user);
		
		Compensation com = new Compensation();
		com.setId(user.getId());
		com.setEvent(1);
		com.setInc(1);
		
		insertPoint(com);
		
	}
	
	// 룰렛 VIEW
	@RequestMapping(value="/roulette", method=RequestMethod.GET)
	public void eventRoulette(Model model, HttpSession session) {
		Users login = (Users) session.getAttribute("logInInfo");
		Compensation com = new Compensation();
		com.setId(login.getId());
		
		System.out.println(checkPC(com));
		
		//현재 포인트, 코인
		model.addAttribute("pointcoin", checkPC(com));
	}
	
	@RequestMapping(value="/roulette", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> rouletteResult(Model model, HttpSession session, String result) {
		
		Users login = (Users) session.getAttribute("logInInfo");
		
		Compensation com = new Compensation();
		com.setId(login.getId());
		com.setEvent(3);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(result.equals("꽝!")) {
			//코인 수 차감
			insertCoin(com);
			map.put("getPoint", 0);
			
		} else {
			String[] arr = result.split(" ");
			com.setInc(Integer.parseInt(arr[0]));
			
			//포인트 업데이트, 코인수 차감
			insertCoin(com);
			insertPoint(com);
			map.put("getPoint", com.getInc());
			
		}

		map.put("pointcoin", checkPC(com));
		return map;
		
	}
	
	// 신기록 VIEW
	@RequestMapping(value="/record", method=RequestMethod.GET)
	public void eventRecordfinal(Model model) {
		
		Map<Integer, List> map = new HashMap<Integer, List>();
		map = eventService.selectRecord();
				
		System.out.println("최종"+ map);

		List list = new ArrayList();
		
		for(int i=1; i<=map.size(); i++) {
			System.out.println("이건 되니 : " + i);
			System.out.println("이거배열안에꺼는" + map.get(i));
			System.out.println(map.get(i).size() + "없나봐");
			for(int j=0; j<map.get(i).size(); j++) {
				System.out.println("이거는 " + j);
				list.add(map.get(i).get(j));
				System.out.println(map.get(i).get(j)+"값이 왜이래");
			}
		}
		model.addAttribute("list",list);
	}
	
	// 초성퀴즈 VIEW - event : 2, 하루 한 번 참여 가능
	@RequestMapping(value="/quiz", method=RequestMethod.GET)
	public void eventQuiz(HttpSession session, Model model) {
		
		Users login = (Users) session.getAttribute("logInInfo");
		SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");
		
		//요일을 1-7으로 반환
		GregorianCalendar cal = new GregorianCalendar();
		
		String today = format.format(new Date());
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", login.getId());
		map.put("dates", today);
		
		
		int day = cal.get(Calendar.DAY_OF_WEEK);
			
		Map<String, Object> quiz = eventService.selectQuiz(day);
		String[] words = ((String) quiz.get("INI")).split("");
		quiz.put("words", words);
		quiz.put("length", words.length);
		
		model.addAttribute("attend", eventService.checkQuiz(map));
		model.addAttribute("quiz", quiz);
		
	}
	
	@RequestMapping(value="/quiz", method=RequestMethod.POST)
	public String quizResult(HttpSession session, String answer) {
		Users login = (Users) session.getAttribute("logInInfo");
		Compensation com = new Compensation();
		com.setId(login.getId());
		com.setEvent(2);
		com.setInc(10);
		
		insertPoint(com);
		
		return "redirect:/event/quiz";
	}
	
}