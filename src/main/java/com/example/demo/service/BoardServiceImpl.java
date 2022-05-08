package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Board;
import com.example.demo.domain.QBoard;
import com.example.demo.domain.Search;
import com.example.demo.persistence.BoardRepository;
import com.querydsl.core.BooleanBuilder;

@Service
public class BoardServiceImpl implements BoardService {
	
	@Autowired	private BoardRepository boardRepo;
	
	public void insertBoard(Board board) {
		boardRepo.save(board);
	}
	
	public void updateBoard(Board board) {
		Board findBoard = boardRepo.findById(board.getSeq()).get();
		
		findBoard.setTitle(board.getTitle());
		findBoard.setContent(board.getContent());
		boardRepo.save(findBoard);
	}
	
	public void deleteBoard(Board board) {
		boardRepo.deleteById(board.getSeq());
	}
	
	public Board getBoard(Board board) {
		return boardRepo.findById(board.getSeq()).get();
	}
	
	public Page<Board> getBoardList(Search search, int page) {
		BooleanBuilder builder = new BooleanBuilder();
		
		QBoard qboard = QBoard.board;
		if(search.getSearchCondition().equals("TITLE")) {
			builder.and(qboard.title.like("%" + search.getSearchKeyword() + "%"));
		} else if(search.getSearchCondition().equals("CONTENT")) {
			builder.and(qboard.content.like("%" + search.getSearchKeyword() + "%"));
		}
		Pageable pageable = PageRequest.of(page, 5, Sort.Direction.DESC, "seq");
		return boardRepo.findAll(builder, pageable);
	}
}
