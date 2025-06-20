package com.boot.board.controller;

import com.boot.board.svc.BoardSVC;
import com.boot.board.vo.Board;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
public class BoardController {
    @Autowired
    BoardSVC svc;

    private String imgsrc = "";

    @GetMapping({"/", "/index"})
    public String home() {
        return "index";
    }

    @GetMapping("/boardForm")
    public String boardFrm() {
        return "boardWrite";
    }

    @RequestMapping(value = "/boardWrite", method = RequestMethod.POST)
    public String boardWrite(Board board, HttpServletRequest request) {

        MultipartFile file = board.getBfile();
        // 실제 빌드 시 경로
//        String uploadDir = "/boardImg/";
        // 테스트 시 경로
        String uploadDir = "src/main/resources/static/boardImg/";


        Path fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        imgsrc = fileStorageLocation.toString();
        System.out.println("파일 저장 경로 : " +fileStorageLocation.toString());
        // 파일 저장 디렉토리가 존재하지 않으면 생성
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            System.out.println("디렉토리 생성 불가");
        }

        if (!file.isEmpty()) {
            // 원본 파일 이름
            String originalName = board.getBfilename();

            // 저장될 이름 (중복 방지를 위해 UUID 사용)
            String storedName = UUID.randomUUID() + "_" + originalName;

            try {
                // 파일 저장
                Path targetLocation = fileStorageLocation.resolve(storedName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                System.out.println("파일 저장 실패");
            }

            board.setBfilenamenew(storedName);
        }
        System.out.println(board);
        int result = svc.boardWrite(board);

        System.out.println(board);
        return "redirect:boardList";
    }

    @GetMapping("/boardList")
    public String boardList(Model model) {
        List<Board> boardList = svc.boardList();
        model.addAttribute("boardList", boardList);
        model.addAttribute("imageUrl", imgsrc);
        return "boardList";
    }

    //파일 다운로드
    @GetMapping("/download/{filename:.+}/{origin_filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, @PathVariable String origin_filename) {
        System.out.println("변경된 파일명: "+filename);
        System.out.println("실제 파일명: "+origin_filename);
        /* Paths.get("/static/boardImg/") => 문자열 경로를 Path 객체로 변환.
         toAbsolutePath() => 프로젝트 기준의 절대 경로로 변환함.
         ▼ 예시:
         "/static/boardImg/"  => "C:\static\boardImg" 반환함
         "static/boardImg/"  => "C:\bwork\board\static\boardImg" 반환함
         normalize() => 경로를 정리해서 보기 좋고 정확하게 만들어 주는 기능
         */
        // 실제 빌드 시 경로
        //String uploadDir = "/boardImg/";
        // 테스트 시 경로
        String uploadDir = "src/main/resources/static/boardImg/";
        Path fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Path filePath = fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + origin_filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

}
