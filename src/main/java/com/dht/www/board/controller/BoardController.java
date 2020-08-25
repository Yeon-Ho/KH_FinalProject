package com.dht.www.board.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dht.www.board.model.service.BoardService;
import com.dht.www.board.model.service.CommentsService;
import com.dht.www.board.model.vo.Board;
import com.dht.www.user.model.vo.Users;

@Controller
@RequestMapping("/board")
public class BoardController {

	@Autowired
	private BoardService boardService;
	
	@Autowired
	private CommentsService commentsService;
	
	// 게시판 목록 조회
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public void list(@RequestParam(required=false, defaultValue="1") int cPage, @RequestParam(required=false, defaultValue="5") int cntPerPage, Model model) {
		
		Map<String, Object> boardListMap = boardService.selectBoardList(cPage, cntPerPage);
		model.addAttribute("boardData", boardListMap);
	}
	
	// 게시판 상세 조회
	@RequestMapping(value="/detail", method=RequestMethod.GET)
	public void detail(@RequestParam int no, Model model) {
		
		Map<String, String> boardMap = boardService.selectBoard(no);
		model.addAttribute("board", boardMap);
		
		Map<String, Object> commentsListMap = commentsService.selectCommentsList(no);
		model.addAttribute("commentsData", commentsListMap);
	}
	
	// 게시글 작성 페이지
	@RequestMapping(value="/write", method=RequestMethod.GET)
	public void write() {
	}
	
	// 게시글 작성
	@RequestMapping(value="/write", method=RequestMethod.POST)
	public String write(Board board, Model model, HttpServletRequest req, HttpSession session) {
		
		String logInId = ((Users) session.getAttribute("logInInfo")).getId();
		board.setId(logInId);
		int res = boardService.insertBoard(board);
		
		if(res > 0) {
			model.addAttribute("url", req.getContextPath()+"/board/list");
		} else {
			model.addAttribute("alertMsg", "게시글 작성에 실패하였습니다.");
			model.addAttribute("url", req.getContextPath()+"/board/list");
		}
		
		return "/common/result";
	}
	
}