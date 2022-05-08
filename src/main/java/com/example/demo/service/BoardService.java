package com.example.demo.service;

import org.springframework.data.domain.Page;

import com.example.demo.domain.Board;
import com.example.demo.domain.Search;

public interface BoardService {
	void insertBoard(Board board);
	
	void updateBoard(Board board);
	
	void deleteBoard(Board board);
	
	Board getBoard(Board board);
	
	Page<Board> getBoardList(Search search, int page);
	
}
