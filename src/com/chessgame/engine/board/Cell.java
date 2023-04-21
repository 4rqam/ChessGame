package com.chessgame.engine.board;

import com.chessgame.engine.piece.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.*;

public abstract class Cell {

    protected final int cellCoordinate;

    private static final Map<Integer, EmptyCell> EMPTY_CELLS_CACHE = createAllPossibleEmptyCells();

    private static Map<Integer, EmptyCell> createAllPossibleEmptyCells() {
        final Map<Integer, EmptyCell> emptyCellMap = new HashMap<>();
        for(int i = 0; i < BoardUtils.NUM_CELLS; i++) {
            emptyCellMap.put(i, new EmptyCell(i));
        }
        return ImmutableMap.copyOf(emptyCellMap);
    }

    public static Cell createCell(final int cellCoordinate, final Piece piece) {
        return piece != null ? new OccupiedCell(cellCoordinate, piece) : EMPTY_CELLS_CACHE.get(cellCoordinate);
    }

    private Cell(final int cellCoordinate) {
        this.cellCoordinate = cellCoordinate;
    }

    public abstract boolean isCellOccupied();

    public abstract Piece getPiece();

    public int getCellCoordinate() {
        return this.cellCoordinate;
    }

    public static final class EmptyCell extends Cell {

        private EmptyCell(final int coordinate) {
            super(coordinate);
        }

        @Override
        public String toString() {
            return "-";
        }

        @Override
        public boolean isCellOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }
    }

    public static final class OccupiedCell extends Cell {

        private final Piece pieceOnCell;

        private OccupiedCell(int cellCoordinate, final Piece pieceOnCell) {
            super(cellCoordinate);
            this.pieceOnCell = pieceOnCell;
        }
        @Override
        public String toString() {
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() :
                    getPiece().toString();
        }

        @Override
        public boolean isCellOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.pieceOnCell;
        }
    }
}
