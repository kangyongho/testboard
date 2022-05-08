package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.domain.Board;
import com.example.demo.domain.Member;
import com.example.demo.domain.Role;
import com.example.demo.persistence.BoardRepository;
import com.example.demo.persistence.MemberRepository;

@Controller
public class SecurityController {
	@Autowired	private MemberRepository memberRepo;
	@Autowired	private BoardRepository boardRepo;
	@Autowired	private PasswordEncoder encoder;
	
	@GetMapping("/system/login")
	public void login( ) {}
	
	@GetMapping("/system/accessDenied")
	public void accessDenied() {}
	
	@GetMapping("/system/logout")
	public void logout() {}
	
	@GetMapping("/admin/adminPage")
	public void admin() {}
	
	@GetMapping("/system/initialize")
	public String initialize() {
		Member member1 = new Member();
		member1.setId("member");
		member1.setPassword(encoder.encode("member321"));
		member1.setName("아이언맨");
		member1.setRole(Role.ROLE_MEMBER);
		member1.setEnabled(true);
		memberRepo.save(member1);
		
		Member member2 = new Member();
		member2.setId("admin");
		member2.setPassword(encoder.encode("admin321"));
		member2.setName("캡틴아메리카");
		member2.setRole(Role.ROLE_ADMIN);
		memberRepo.save(member2);
		
		for (int i=1; i<=101; i++) {
			Board board = new Board();
			board.setMember(member1);
			board.setTitle(member1.getName() + "가 등록한 게시글 " + i);
			board.setContent(member1.getName() + "가 등록한 게시글 " + i);
			boardRepo.save(board);
		}
		
		for (int i=1; i<=51; i++) {
			Board board = new Board();
			board.setMember(member2);
			board.setTitle(member2.getName() + "가 등록한 게시글 " + i);
			board.setContent(member2.getName() + "가 등록한 게시글 " + i);
			boardRepo.save(board);
		}
		return "redirect:/";
	}
}
