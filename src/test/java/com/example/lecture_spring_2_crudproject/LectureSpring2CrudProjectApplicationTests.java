package com.example.lecture_spring_2_crudproject;

import com.example.lecture_spring_2_crudproject.entity.account.Member;
import com.example.lecture_spring_2_crudproject.repository.account.MemberRepository;
import com.example.lecture_spring_2_crudproject.service.openAPI.PublicAPI;
import com.example.lecture_spring_2_crudproject.service.textTransfer.Selenium;
import com.example.lecture_spring_2_crudproject.service.textTransfer.SeleniumExample;
import com.example.lecture_spring_2_crudproject.service.textTransfer.TextTransfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootTest
class LectureSpring2CrudProjectApplicationTests {



    @Autowired
    SeleniumExample seleniumExample;

    @Autowired
    Selenium selenium;
    @Autowired
    TextTransfer textTransfer;

    @Autowired
    PublicAPI publicAPI;

    //API 받아오기 테스트
    @Test
    void apiTest() {
        publicAPI.testAPI();
    }

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("저장, 데이터가 잘 들어갔는지 확인")
    void contextSave() {
        //Setter로 엔티티를 생성하고 repositoy가 정상 작동하는지 확인
        Member member = new Member();
        //클라이언트에서 controller에 데이터를 전달하는 내용을 setter를 통해 대체
        member.setId("humanClass4");
        member.setPassword("12341234@");
        member.setEmail("class4@123.com");
        //memberRepository의 save메서드가 정상 동작하는지 확인
        memberRepository.save(member);
    }

    //substring과 split
    @Test
    void textTest() throws Exception {
        textTransfer.transferText3Word("abcdefg@gmil.com");
    }

    @Test
    void Scraping_win() {
        selenium.scraping();
    }

    //스크래핑 테스트
    @Test
    void ScrapingTest() {
        seleniumExample.scraping();
    }



}
