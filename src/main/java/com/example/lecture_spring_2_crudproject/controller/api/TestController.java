package com.example.lecture_spring_2_crudproject.controller.api;

import com.example.lecture_spring_2_crudproject.entity.account.Member;
import com.example.lecture_spring_2_crudproject.entity.board.Board;
import com.example.lecture_spring_2_crudproject.entity.customDto.CustomAPIDtoExample;
import com.example.lecture_spring_2_crudproject.entity.customDto.CustomStudentData;
import com.example.lecture_spring_2_crudproject.entity.customDto.Student;
import com.example.lecture_spring_2_crudproject.service.account.MemberService;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

//api 데이터 전송 컨트롤로 메서드들은 @responsBody 속성 가짐
//@RestController
@Controller
public class TestController {

    private final MemberService memberService;

    @Autowired
    protected TestController(MemberService memberService) {
        this.memberService = memberService;
    }

    //CRUD restFullApi rest하게 API를 만들자 (암묵적인 규칙)
    //Create, Read, Update, Delete
    //data를 전달하는 controller URI주소 만들기
    @ResponseBody
    @RequestMapping("/read/alldata")
    public CustomStudentData readAlldata() {

        Student jskim = new Student();

        jskim.setName("김준석");
        jskim.setGroup(1);
        jskim.setPosition("backend");
        jskim.setMemo("한국어~");

        Student jspark = new Student();

        jspark.setName("박준석");
        jspark.setGroup(5);
        jspark.setPosition("frontend");
        jspark.setMemo("영어~");

        CustomStudentData cst = new CustomStudentData();

        List<Student> studentList = new ArrayList<>();
        studentList.add(jskim);
        studentList.add(jspark);
        cst.setStudentList(studentList);

        return cst;
    }

    @ResponseBody
    @RequestMapping("/read/alltest")
    public List<Member> readAllTest() {

        return memberService.getMemberListEncodingByMemberList(
                memberService.getMemberList());
    }

    @ResponseBody
    @RequestMapping("/test1")
    public CustomAPIDtoExample test1(){

        CustomAPIDtoExample blog = new CustomAPIDtoExample();

        blog.setTitle("테스트1");
        blog.setContent("테스트1 내용");

        return blog;

    }

    @ResponseBody
    @RequestMapping("/rps/v002")
    public String test2(){
        JsonObject obj =new JsonObject();

        obj.addProperty("rps", "가위");

        JsonObject data = new JsonObject();

        data.addProperty("time", "12:00");
        data.addProperty("model_version", "0.0.2");
        obj.add("data", data);

        return obj.toString();
    }

//    @ResponseBody
//    @RequestMapping("/rps/request")
//    public CustomAPIDtoExample rpsV0001(){
//
//        JsonObject obj =new JsonObject();
//
//        obj.addProperty("title", "테스트2");
//        obj.addProperty("content", "테스트2 내용");
//
//        JsonObject data = new JsonObject();
//
//        data.addProperty("time", "12:00");
//
//        obj.add("data", data);
//
//        return obj.toString();
//
//    }
}
