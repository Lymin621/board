package com.boot.board.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class Board {
    private MultipartFile bfile ;
    private String bno ;
    private String bid ;
    private String bnm  ;
    private String btitle ;
    private String bcontent  ;
    private String bfilename ;
    private String bregdate ;
    private String bfilenamenew ;
}