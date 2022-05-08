package com.example.demo.persistence;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.domain.Member;

public interface MemberRepository extends CrudRepository<Member, String>{
	
}
