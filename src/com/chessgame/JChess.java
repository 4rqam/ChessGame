package com.chessgame;

import com.chessgame.engine.board.Board;
import com.chessgame.gui.Table;

public class JChess {

    public static void main(String [] args) {

        Board board = Board.createStandardBoard();

        System.out.println(board);

        Table table = new Table(); }
}
