package com.example.lecture_spring_2_crudproject.controller.board;

import com.example.lecture_spring_2_crudproject.entity.account.Member;
import com.example.lecture_spring_2_crudproject.entity.board.Board;
import com.example.lecture_spring_2_crudproject.entity.board.Comments;
import com.example.lecture_spring_2_crudproject.entity.board.FileEntity;
import com.example.lecture_spring_2_crudproject.entity.customDto.CustomDtoSortPages;
import com.example.lecture_spring_2_crudproject.entity.data.FileUploadEntity;
import com.example.lecture_spring_2_crudproject.service.board.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.*;
import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.google.common.io.ByteStreams.toByteArray;
import static org.apache.tomcat.util.http.fileupload.IOUtils.*;

/**
 * @description : 게시판 컨트롤러
 **/

@Controller
@Slf4j
@RequestMapping(path = "/board")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    protected BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/insertComments")
    public String insertComments(Board board, Model model) {
        System.out.println(board.getTitle());
        model.addAttribute("board", board);
        return "/board/insertComments";
    }

    @GetMapping("/music")
    public String getMusicPage(Model model) {

        return "/board/music";
    }

    @PostMapping("/insertComments")
    public String insertComments(@RequestParam("board_title")String boardTitle, Comments comments, Model model) {

        System.out.println("------inertComments---------");
        System.out.println(comments.getBoard_title());
        System.out.println(comments.getComments_content());
        boardService.insertComment(comments);
        return "redirect:/board/getBoardList";
    }

    //board Seq전달하면 전체 comments를 불러오는 controller method
    @GetMapping("/getCommentsList")
    public String getCommentsList(Comments comments, Model model) {
        System.out.println("-------getCommentsList-------");
        System.out.println(comments.getBoard_title());
        List<Comments> checkCommentsList = boardService.getAllComments(comments);

        model.addAttribute("commentsList", checkCommentsList);
        return "/board/getCommentsList";
    }

    @GetMapping("/getBoardList")
    public String getBoardList(Model model, Board board) {
        System.out.println("-------------------");
        List<Board> boardList = boardService.getBoardList(board);
        model.addAttribute("boardList", boardList);
        return "/board/getBoardList";
    }

    @GetMapping("/insertBoard")
    public String insertBoard() {
        System.out.println("------insertBoard_get-------------");
        return "/board/insertBoard";
    }

    @PostMapping("/insertBoard")
    public String insertBoard(Board board, @Nullable@RequestParam("uploadfile")MultipartFile[] uploadfile) {
        //@Nullable@RequestParam("uploadfile")MultipartFile[] :
        //MultipartFile를 클라이언트에서 받아오고, 데이터가 없더라도 허용 (@Nullable)
        try {
            //boardService.insertBoard 메서드에서는 DB에 데이터를 저장하고 저장된 board_seq를 리턴 받음
            Long board_seq = boardService.insertBoard(board);

            List<FileUploadEntity> list = new ArrayList<>();
            for(MultipartFile file : uploadfile) {
                //MultipartFile로 클라이언트에서 온 데이터가 무결성 조건에 성립을 안하거나 메타데이터가 없거나 문제가 생길 여지를 if문으로 처리
                if(!file.isEmpty()) {
                    FileUploadEntity entity = new FileUploadEntity(null,
                            UUID.randomUUID().toString(),
                            file.getContentType(),
                            file.getName(),
                            file.getOriginalFilename(),
                            board_seq
                    );
                    //fileuploadtable에 데이터 저장
                    boardService.insertFileUploadEntity(entity);
                    list.add(entity);
                    File newFileName = new File(entity.getUuid()+"_"+entity.getOriginalFilename());
                    //서버에 이미지 파일 업로드(저장)
                    file.transferTo(newFileName);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }


        return "redirect:/board/getBoardList";
    }

    @GetMapping("/getBoard")
    public String getBoard(Board board, Model model) {

        List<FileUploadEntity> fileUploadEntity = boardService.getFileUploadEntity2(board.getSeq());
        List<String> path = new ArrayList<>();
        for (FileUploadEntity fe : fileUploadEntity) {
            String savePath = "/board/image/"+fe.getUuid()+"_"+fe.getOriginalFilename();
            path.add(savePath);
        }

        model.addAttribute("board", boardService.getBoard(board));
        model.addAttribute("boardPrv", boardService.getPagesSortIndex(board));
        model.addAttribute("imgLoading", path);
//        model.addAttribute("imgLoading", path+"/filer");
        return "/board/getBoard";
    }

    @PostMapping ("/updateBoard")
    public String updateBoard(Board board) {
        System.out.println("----------updateBoard---------");
        System.out.println(board.getContent());
        System.out.println(board.getSeq());
        boardService.updateBoard(board);
        return "redirect:/board/getBoard?seq="+board.getSeq();
    }

    @GetMapping("/updateBoard")
    public String updateBoardView(Board board, Model model) {
        System.out.println("-------------------");
        model.addAttribute("board", boardService.getBoard(board));
        return "/board/insertBoard";
    }

    @GetMapping("/deleteBoard")
    public String deleteBoard(Board board) {
        System.out.println("--------boarde delete-----------");
        System.out.println(board.getSeq());
        boardService.deleteBoard(board);
        return "redirect:/board/getBoardList";
    }


    @GetMapping("/viewUserWriteBoard")
    public String viewUserWriteBoard(Member member, Model model) {
        System.out.println("-------view-------");
        System.out.println(member.getId());
        model.addAttribute("boardList",
                boardService.getBoardListAllBoardListByMemberId(member));
        return "/board/getBoardList";
    }

    @GetMapping("/getAllUserBoardList")
    public String AllUsersBoard(Model model) {

//        List<Board> board = null;
//        List<Member> member = null;

//        for(List<Object> result : boardService.getBoardAndMemberUsersBoard()) {
//            board = (List<Board>) result.get(0);
//            member = (List<Member>) result.get(1);
//        }

        System.out.println(boardService.getBoardAndMemberUsersBoard().size());

//            board = (List<Board>) boardService.getBoardAndMemberUsersBoard().get(0);
//            member = (List<Member>) boardService.getBoardAndMemberUsersBoard().get(1);

        return "index";
    }

    @GetMapping("/sortTest")
    public String sortTest(Board board, Model model) {

        CustomDtoSortPages customDtoSortPages = boardService.getPagesSortIndex(board);
        System.out.println(customDtoSortPages.getNEXT_SUBJECT());
        System.out.println(customDtoSortPages.getNEXTID());
        System.out.println(customDtoSortPages.getPREV_SUBJECT());
        System.out.println(customDtoSortPages.getPREVID());

//        model.addAttribute("sortBoard", customDtoSortPages);
        return "/board/getBoardList";
    }

//    application/x-www-form-urlencoded
//    multipart/form-data
//    @PostMapping("/updateImage")
//    public String updateImage(@RequestParam("uploadfile")MultipartFile[] uploadfile, Model model) throws IOException {
//        log.info("dawda");
//        List<FileEntity> list = new ArrayList<>();
//        for(MultipartFile file : uploadfile) {
//            if(!file.isEmpty()) {
//                FileEntity dto = new FileEntity(null, UUID.randomUUID().toString(), file.getContentType(), file.getName(),file.getOriginalFilename());
//                list.add(dto);
//                File newFileName = new File(dto.getUuid()+"_"+dto.getName()+".png");
//                file.transferTo(newFileName);
//
//                //이미지 사이즈 변경
////                boolean isConvert = ImageUtils.converFmt();
////                if(isConvert) {
////                    ImageUtils.resizeImage(newFileName.getPath(), 60, 60);
////                }
//            }
//        }
//        return "/board/getBoardList";
//    }

    //*client에서 server로 이미지파일 전송(데이터 전송)
    //html form태그에 upload버튼으로 이미지 데이터 전송(MultipartFile) > Entity기준으로 데이터 정보를 전달
    //-server는 이미지파일을 특정 폴더에 저장
    //장점 : 서버에 원본 이미지 파일을 저장하므로 필요할 때 서버에서 바로 전달 받을 수 있음 = db에 부담감이 줄어듬
    //단점 : 다수의 서버에 이미지 파일을 저장할 경우, 동일한 이미지 데이터 처리에 대한 이슈 발생 > UUID를 통해 이미지 이름을 구분
    //-server는 이미지 파일을 byte코드로 db에 저장
    //장점 : 이미지 데이터를 한 곳에 저장하고 관리
    //단점 : DB에 많은 부하가 걸림, 데이터 저장 포맷의 한계. (oracle 기준으로 Blob 단위로 저장할 때 4gb한계에 이슈)
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("uploadfile")MultipartFile[] uploadfile,
                             @RequestParam("seq")Long input_seq) throws IOException {
        //@RequestParam("writer") = 클라이언트 html의 input tag의 name(key값)인 writer를 controller에서 받아옴
        //매개변수 String input_writer로 전달
//        log.info(input_writer);
        //multipartfile을 클라이언트에서 서버로 RequestParam데이터 받아옴 name=uploadfile
//        System.out.println("test");
        //@Slf4j Lombook라이브러리로 log데이터 찍음
        //info / error / debug 단위가 있고 단위마다 필터링하여 정보를 수집하고 관리 가능
        log.info("img load session");
        //multipartfile데이터를 수집하여 entity FileUploadEntity에 데이터 저장
        List<FileUploadEntity> list = new ArrayList<>();

        for(MultipartFile file : uploadfile) {
            //MultipartFile file이 있을 때까지 작업 진행 (파일이 없으면 if문 실행 안 됨)
            if(!file.isEmpty()) {
                //MultipartFile의 정보를 dto에 저장
                //file.get~ 메서드는 MultipartFile (이미지) 내부에 있는 메타데이터를 가져오는 메서드
                //input_writer는 클라이언트에서 데이터를 직접 전달하는 string데이터
                FileUploadEntity entity = new FileUploadEntity(null,
                        UUID.randomUUID().toString(),
                        file.getContentType(),
                        file.getName(),
                        file.getOriginalFilename(),
                        input_seq
                        );
                Long output = boardService.insertFileUploadEntity(entity);
                log.info("seq check!");
                log.info(output.toString());
                list.add(entity);
                File newFileName = new File(entity.getUuid()+"_"+entity.getOriginalFilename());
                //file을 서버에 저장하는 스트림행위는 서버가 성공할지 여부를 체크하므로 exception처리 필요
                //메서드에 throws IOException 처리 = try catch처리 필요
                file.transferTo(newFileName);
            }
        }
        return "/board/getBoardList";
    }


    //server에서 client로 이미지 전송
    //springboot에서 URL주소를 통해 이미지를 받음, InputStream을 통해 파일을 http프로토콜에 전달하여 클라이언트에게 전송

//    @GetMapping("/viewImage/{seq}")
//    public ResponseEntity<byte[]> viewImage(@PathVariable("seq")String board_seq) throws IOException {
//
//        List<FileUploadEntity> fileUploadEntity = boardService.getFileUploadEntity(board_seq);
//        //ResponseEntity<byte[]> : http프로토콜을 통해서 byte데이터를 전달하는 객체, byte(소문자=기본타입) []배열
//        //@PathVariable : URL주소의 값을 받아옴
//        String path = "/Users/js/Cleancode/lecture_spring_2_crudProject/src/main/resources/static/upload/"+fileUploadEntity.getUuid()+"_"+fileUploadEntity.getOriginalFilename();
//        log.info(path);
//        //데이터(이미지)를 전송하기 위한 객체로써 java에서는 항상 데이터를 스트림타입으로 전달
////        InputStream inputStream = new FileInputStream(path);
////        //byte배열로 변환
////        byte[] imgByteArr = toByteArray(inputStream);
////        inputStream.close();
//
//        FileInputStream fis = new FileInputStream(path); // 원본 파일 명
//        //경로가 가르키는 파일을 바이트 스트림으로 읽기
////        int read = 0;
////        while((read= fis.read())!=-1) {
////            log.info(String.valueOf(read));
////        }
//
//        BufferedInputStream bis = new BufferedInputStream(fis);
//        //바이트 단위로 파일을 읽어오는 버퍼 스트림으로 가져오기
//
//        byte[] imgByteArr = bis.readAllBytes();
//        for(int i = 0; i<imgByteArr.length; i++ ) {
//            if(i==20) {
////                log.info(imgByteArr.);
//            }
//
//        }
////        log.debug(imgByteArr.toString());
//        //ResponseEntity를 통해 http프로토콜로 클라이언트에게 데이터 전송
//        return new ResponseEntity<byte[]>(imgByteArr, HttpStatus.OK);
//    }


//
//    @GetMapping(value = "/image/{imagename}", produces = MediaType.IMAGE_JPEG_VALUE)
//    public ResponseEntity<byte[]> imageSearch(@PathVariable("imagename") String imagename) throws IOException {
//        InputStream imageStream = new FileInputStream("/Users/js/Cleancode/lecture_spring_2_crudProject/src/main/resources/static/upload/" + imagename);
//        byte[] imageByteArray = toByteArray(imageStream);
//        imageStream.close();
//        return new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);
//    }

    @GetMapping(value = "/image/{imagename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> imageLoading(@PathVariable("imagename")String imgname) throws IOException {
        //ResponseEntity<byte[]> : 메서드 리턴타임으로 이미지 데이터를 송신하기 위한 객체<바이트 배열>
        //throws IOException : 스트림방식으로 데이터를 전송할 때 도중에 오류가 날 경우를 찾기 위해서 선언한 Exception

        String path = "/Users/js/Cleancode/lecture_spring_2_crudProject/src/main/resources/static/upload/"+imgname;
        //File을 컴퓨터가 이해하기 위해서 Stream 배열을 만들어서 작업
        //객체(데이터 저장) : String, int, double
        //Stream객체는 파일을 컴퓨터가 cpu에서 바로 읽어들일 수 있도록 하는 객체
        FileInputStream fis = new FileInputStream(path);
        //Buffered : CPU에서 데이터 읽어올 때 메모리와 캐시 사이에서 CPU와의 속도 차이를 줄이기 위한 중간 저장 위치
        BufferedInputStream bis = new BufferedInputStream(fis);
        //byte배열로 전환하여 ResponseEntity를 통해 클라이언트에게 데이터 전달
        //HTTP프로토콜은 바이트 단위(배열)로 데이터를 주고 받음
        byte[] imgByteArr = bis.readAllBytes();
        //ResponseEntity를 통해 http프로토콜로 클라이언트에게 데이터 전송

        //http프로토콜은 바이트 배열로 데이터를 주고 받기 때문에 stream이나 버퍼를 통해 전환
        return new ResponseEntity<byte[]>(imgByteArr, HttpStatus.OK);

    }
//
//    @GetMapping("/viewImage/{imgname}")
//    public ResponseEntity<byte[]> viewImage(@PathVariable("imgname")String input_imgName) throws IOException {
//        //ResponseEntity<byte[]> : http프로토콜을 통해서 byte데이터를 전달하는 객체, byte(소문자=기본타입) []배열
//        //@PathVariable : URL주소의 값을 받아옴
//        String path = "/Users/js/Cleancode/lecture_spring_2_crudProject/src/main/resources/static/upload/"+input_imgName;
//        //데이터(이미지)를 전송하기 위한 객체로써 java에서는 항상 데이터를 스트림타입으로 전달
////        InputStream inputStream = new FileInputStream(path);
////        //byte배열로 변환
////        byte[] imgByteArr = toByteArray(inputStream);
////        inputStream.close();
//
//        FileInputStream fis = new FileInputStream(path); // 원본 파일 명
//        BufferedInputStream bis = new BufferedInputStream(fis);
//        byte[] imgByteArr = bis.readAllBytes();
//        //ResponseEntity를 통해 http프로토콜로 클라이언트에게 데이터 전송
//        return new ResponseEntity<byte[]>(imgByteArr, HttpStatus.OK);
//    }

    @GetMapping(value = "/image/{imagename}/filer", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> imagefilter(@PathVariable("imagename") String imagename) throws IOException {
        InputStream imageStream = new FileInputStream("/Users/js/Cleancode/lecture_spring_2_crudProject/src/main/resources/static/upload/" + imagename);
        byte[] imageByteArray = toByteArray(imageStream);
        imageStream.close();
        log.info("image process");
//        log.info(Arrays.toString(imageByteArray));
//        for(int i =0; i<imageByteArray.length; i++) {
//            if(!(imageByteArray[i] == 0)) {
//                imageByteArray[i] = 0;
//            }
//        }
        return new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);
    }

//    @GetMapping("/display")
//    public ResponseEntity<Resource> display(@RequestParam("filename") String filename) {
//        String path = "C:\\Temp\\upload\\";
//        String folder = "";
//        Resource resource = new FileSystemResource(path + folder + filename);
//        if(!resource.exists())
//            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
//        HttpHeaders header = new HttpHeaders();
//        Path filePath = null;
//        try{
//            filePath = Paths.get(path + folder + filename);
//            header.add("Content-type", Files.probeContentType(filePath));
//        }catch(IOException e) {
//            e.printStackTrace();
//        }
//        return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);

}
