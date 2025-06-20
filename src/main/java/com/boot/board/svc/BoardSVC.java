package com.boot.board.svc;

import com.boot.board.mapper.BoardMapper;
import com.boot.board.vo.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardSVC {
    @Autowired
    BoardMapper mapper;

    public int boardWrite(Board board){
        return mapper.boardWrite(board);
    }

    public List<Board> boardList(){
        return mapper.boardList();
    }
}
