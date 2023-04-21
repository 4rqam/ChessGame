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

public class BlackPlayer extends Player {
    public BlackPlayer(final Board board,
                       final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {

        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentsLegals) {
        final List<Move> kingCastles = new ArrayList<>();

        if(this.playerKing.isFirstMove() && !this.isInCheck()) {
            if(!this.board.getCell(5).isCellOccupied() && !this.board.getCell(6).isCellOccupied()) {
                final Cell rookCell = this.board.getCell(7);

                if(rookCell.isCellOccupied() && rookCell.getPiece().isFirstMove()) {
                    if(Player.calculateAttackOnCell(5, opponentsLegals).isEmpty() &&
                            Player.calculateAttackOnCell(6, opponentsLegals).isEmpty() &&
                            rookCell.getPiece().getPieceType().isRook()) {


                        kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 6, (Rook) rookCell.getPiece(), rookCell.getCellCoordinate(), 5));
                    }

                }
            }

            if(!this.board.getCell(1).isCellOccupied() && !this.board.getCell(2).isCellOccupied() && this.board.getCell(3).isCellOccupied()) {
                final Cell rookCell = this.board.getCell(0);

                if(rookCell.isCellOccupied() && rookCell.getPiece().isFirstMove() && Player.calculateAttackOnCell(2, opponentsLegals).isEmpty() &&
                Player.calculateAttackOnCell(3, opponentsLegals).isEmpty() &&
                rookCell.getPiece().getPieceType().isRook()) {
                    kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 2, (Rook) rookCell.getPiece(), rookCell.getCellCoordinate(), 3));
                }
            }

        }



        return ImmutableList.copyOf(kingCastles);    }
}
