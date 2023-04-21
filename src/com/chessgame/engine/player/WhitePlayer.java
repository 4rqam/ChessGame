package com.chessgame.engine.player;

import com.chessgame.engine.Alliance;
import com.chessgame.engine.board.Board;
import com.chessgame.engine.board.Cell;
import com.chessgame.engine.board.Move;
import com.chessgame.engine.piece.Piece;
import com.chessgame.engine.piece.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chessgame.engine.board.Move.*;

public class WhitePlayer extends Player{

    public WhitePlayer(final Board board,
                       final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {

        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);

    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentsLegals) {

        final List<Move> kingCastles = new ArrayList<>();

        if(this.playerKing.isFirstMove() && !this.isInCheck()) {
            if(!this.board.getCell(61).isCellOccupied() && !this.board.getCell(62).isCellOccupied()) {
                final Cell rookCell = this.board.getCell(63);

                if(rookCell.isCellOccupied() && rookCell.getPiece().isFirstMove()) {
                    if(Player.calculateAttackOnCell(61, opponentsLegals).isEmpty() &&
                    Player.calculateAttackOnCell(62, opponentsLegals).isEmpty() &&
                    rookCell.getPiece().getPieceType().isRook()) {


                        kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 62, (Rook)rookCell.getPiece(), rookCell.getCellCoordinate(), 61));
                    }

                }
            }

            if(!this.board.getCell(59).isCellOccupied() && !this.board.getCell(58).isCellOccupied() && this.board.getCell(57).isCellOccupied()) {
                final Cell rookCell = this.board.getCell(56);

                if(rookCell.isCellOccupied() && rookCell.getPiece().isFirstMove() && Player.calculateAttackOnCell(58, opponentsLegals).isEmpty() &&
                Player.calculateAttackOnCell(59, opponentsLegals).isEmpty() &&
                rookCell.getPiece().getPieceType().isRook()) {
                    kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 58, (Rook)rookCell.getPiece(), rookCell.getCellCoordinate(), 59));
                }
            }

        }



        return ImmutableList.copyOf(kingCastles);
    }
}
