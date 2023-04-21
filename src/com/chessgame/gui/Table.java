package com.chessgame.gui;

import com.chessgame.engine.board.Board;
import com.chessgame.engine.board.BoardUtils;
import com.chessgame.engine.board.Cell;
import com.chessgame.engine.board.Move;
import com.chessgame.engine.piece.Piece;
import com.chessgame.engine.player.MoveTransition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {

    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private Board chessBoard;

    private Cell sourceCell;
    private Cell destinationCell;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection; //fixed by initializing

    private boolean highlightLegalMoves;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension CELL_PANEL_DIMENSION = new Dimension(10, 10);
    private static String defaultPieceImagesPath = "ChessPieces/";

    //private final Color lightCellColor = color.decode("")
    //private final Color darkCellColor = color.decode("")

    public Table() {
        this.gameFrame = new JFrame("Chess");
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = false;
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up that pgn file!");
            }
        });

        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);

            }
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);

        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighLighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", false);

        legalMoveHighLighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighLighterCheckbox.isSelected();
            }
        });

        preferencesMenu.add(legalMoveHighLighterCheckbox);

        return preferencesMenu;
    }

    public enum BoardDirection {

        NORMAL {
            @Override
            List<CellPanel> traverse(List<CellPanel> boardCells) {
                return boardCells;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<CellPanel> traverse(List<CellPanel> boardCells) {
                List<CellPanel> reversedList = new ArrayList<>(boardCells);
                Collections.reverse(reversedList);
                return reversedList;
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<CellPanel> traverse(final List<CellPanel> boardCells);
        abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel {
        final List<CellPanel> boardCells;

        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardCells = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_CELLS; i++) {
                final CellPanel cellPanel = new CellPanel(this, i);
                this.boardCells.add(cellPanel);
                add(cellPanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            for (final CellPanel cellPanel : boardDirection.traverse(boardCells)) {
                cellPanel.drawCell(board);
                add(cellPanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog {
        private final List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int index) {
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }
    }



    private class CellPanel extends JPanel {

        private final int cellId;

        CellPanel(final BoardPanel boardPanel, final int cellId) {
            super(new GridBagLayout());
            this.cellId = cellId;
            setPreferredSize(CELL_PANEL_DIMENSION);
            assignTileColor();
            assignCellPieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {

                    if(isRightMouseButton(e)) {

                        sourceCell = null;
                        destinationCell = null;
                        humanMovedPiece = null;

                    } else if (isLeftMouseButton(e)) {
                            if(sourceCell == null) {
                                sourceCell = chessBoard.getCell(cellId);
                                humanMovedPiece = sourceCell.getPiece();
                                if (humanMovedPiece == null) {
                                    sourceCell = null;
                                }
                            } else {
                                destinationCell = chessBoard.getCell(cellId);
                                final Move move = Move.MoveFactory.createMove(chessBoard, sourceCell.getCellCoordinate(), destinationCell.getCellCoordinate());
                                final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                                if(transition.getMoveStatus().isDone()) {
                                    chessBoard = transition.getTransitionBoard();
                                    moveLog.addMove(move);
                                }
                                sourceCell = null;
                                destinationCell = null;
                                humanMovedPiece = null;
                            }
                            SwingUtilities.invokeLater(new Runnable() {
                               @Override
                                public void run() {
                                   gameHistoryPanel.redo(chessBoard, moveLog);
                                   takenPiecesPanel.redo(moveLog);
                                   boardPanel.drawBoard(chessBoard);
                                }
                            });

                    }


                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });

            validate();
        }


        public void drawCell(final Board board) {
            assignTileColor();
            assignCellPieceIcon(board);
            validate();
            repaint();
        }

        private void assignCellPieceIcon(final Board board) {
            this.removeAll();
            if(board.getCell(this.cellId).isCellOccupied()){
                try {
                    final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath + board.getCell(this.cellId).getPiece().getPieceAlliance().toString().substring(0,1) +
                            board.getCell(this.cellId).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        private void highLightLegalMoves(final Board board) {
            if(highlightLegalMoves) {
                for (final Move move : pieceLegalMoves(board)) {
                    if(move.getDestinationCoordinate() == this.cellId) {
                        try{
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("ChessPieces/highlightGreen.gif")))));
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        private void assignTileColor() {
            if (BoardUtils.EIGHTH_RANK[this.cellId] ||
                    BoardUtils.SIXTH_RANK[this.cellId] ||
                    BoardUtils.FOURTH_RANK[this.cellId] ||
                    BoardUtils.SECOND_RANK[this.cellId]) {
                setBackground(this.cellId % 2 == 0 ? Color.lightGray : Color.darkGray);
            } else if (BoardUtils.SEVENTH_RANK[this.cellId] ||
                    BoardUtils.FIFTH_RANK[this.cellId] ||
                    BoardUtils.THIRD_RANK[this.cellId] ||
                    BoardUtils.FIRST_RANK[this.cellId]) {
                setBackground(this.cellId % 2 != 0 ? Color.lightGray : Color.darkGray);
            }

        }

    }
}



