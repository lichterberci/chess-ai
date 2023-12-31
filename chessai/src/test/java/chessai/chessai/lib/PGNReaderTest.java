package chessai.chessai.lib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PGNReaderTest {

    @Test
    void parseString() {

        String pgnString = """
                [Event "Third Rosenwald Trophy"]
                [Site "New York, NY USA"]
                [Date "1956.10.17"]
                [EventDate "1956.10.07"]
                [Round "8"]
                [Result "0-1"]
                [White "Donald Byrne"]
                [Black "Robert James Fischer"]
                [ECO "D92"]
                [WhiteElo "?"]
                [BlackElo "?"]
                [PlyCount "82"]
                                
                1. Nf3 Nf6 2. c4 g6 3. Nc3 Bg7 4. d4 O-O 5. Bf4 d5 6. Qb3 dxc4 7. Qxc4 c6 8. e4 Nbd7 9. Rd1 Nb6 10. Qc5 Bg4 11. Bg5 Na4 12. Qa3 Nxc3 13. bxc3 Nxe4 14. Bxe7 Qb6 15. Bc4 Nxc3 16. Bc5 Rfe8+ 17. Kf1 Be6 18. Bxb6 Bxc4+ 19. Kg1 Ne2+ 20. Kf1 Nxd4+ 21. Kg1 Ne2+ 22. Kf1 Nc3+ 23. Kg1 axb6 24. Qb4 Ra4 25. Qxb6 Nxd1 26. h3 Rxa2 27. Kh2 Nxf2 28. Re1 Rxe1 29. Qd8+ Bf8 30. Nxe1 Bd5 31. Nf3 Ne4 32. Qb8 b5 33. h4 h5 34. Ne5 Kg7 35. Kg1 Bc5+ 36. Kf1 Ng3+ 37. Ke1 Bb4+ 38. Kd1 Bb3+ 39. Kc1 Ne2+ 40. Kb1 Nc3+ 41. Kc1 Rc2# 0-1
                """;

        PGNReader reader = new PGNReader();

        assertDoesNotThrow(() -> reader.parseString(pgnString));
    }

    @Test
    void getBoards() {

        String pgnString = """
                [Event "Third Rosenwald Trophy"]
                [Site "New York, NY USA"]
                [Date "1956.10.17"]
                [EventDate "1956.10.07"]
                [Round "8"]
                [Result "0-1"]
                [White "Donald Byrne"]
                [Black "Robert James Fischer"]
                [ECO "D92"]
                [WhiteElo "?"]
                [BlackElo "?"]
                [PlyCount "82"]
                                
                1. Nf3 Nf6 2. c4 g6 3. Nc3 Bg7 4. d4 O-O 5. Bf4 d5 6. Qb3 dxc4 7. Qxc4 c6 8. e4 Nbd7 9. Rd1 Nb6 10. Qc5 Bg4 11. Bg5 Na4 12. Qa3 Nxc3 13. bxc3 Nxe4 14. Bxe7 Qb6 15. Bc4 Nxc3 16. Bc5 Rfe8+ 17. Kf1 Be6 18. Bxb6 Bxc4+ 19. Kg1 Ne2+ 20. Kf1 Nxd4+ 21. Kg1 Ne2+ 22. Kf1 Nc3+ 23. Kg1 axb6 24. Qb4 Ra4 25. Qxb6 Nxd1 26. h3 Rxa2 27. Kh2 Nxf2 28. Re1 Rxe1 29. Qd8+ Bf8 30. Nxe1 Bd5 31. Nf3 Ne4 32. Qb8 b5 33. h4 h5 34. Ne5 Kg7 35. Kg1 Bc5+ 36. Kf1 Ng3+ 37. Ke1 Bb4+ 38. Kd1 Bb3+ 39. Kc1 Ne2+ 40. Kb1 Nc3+ 41. Kc1 Rc2# 0-1
                """;

        PGNReader reader = new PGNReader();

        assertDoesNotThrow(() -> reader.parseString(pgnString));

        assertEquals(83, reader.getBoards().size());

        assertEquals("rnbq1rk1/ppppppbp/5np1/8/2PP4/2N2N2/PP2PPPP/R1BQKB1R w KQ - 1 5", reader.getBoards().get(8).getFENString());
        assertEquals("1Q6/5pk1/2p3p1/1pbbN2p/4n2P/8/r5P1/5K2 b - - 5 36", reader.getBoards().get(71).getFENString());
    }


    @Test
    void getMoves() {

        String pgnString = """
                [Event "Third Rosenwald Trophy"]
                [Site "New York, NY USA"]
                [Date "1956.10.17"]
                [EventDate "1956.10.07"]
                [Round "8"]
                [Result "0-1"]
                [White "Donald Byrne"]
                [Black "Robert James Fischer"]
                [ECO "D92"]
                [WhiteElo "?"]
                [BlackElo "?"]
                [PlyCount "82"]
                                
                1. Nf3 Nf6 2. c4 g6 3. Nc3 Bg7 4. d4 O-O 5. Bf4 d5 6. Qb3 dxc4 7. Qxc4 c6 8. e4 Nbd7 9. Rd1 Nb6 10. Qc5 Bg4 11. Bg5 Na4 12. Qa3 Nxc3 13. bxc3 Nxe4 14. Bxe7 Qb6 15. Bc4 Nxc3 16. Bc5 Rfe8+ 17. Kf1 Be6 18. Bxb6 Bxc4+ 19. Kg1 Ne2+ 20. Kf1 Nxd4+ 21. Kg1 Ne2+ 22. Kf1 Nc3+ 23. Kg1 axb6 24. Qb4 Ra4 25. Qxb6 Nxd1 26. h3 Rxa2 27. Kh2 Nxf2 28. Re1 Rxe1 29. Qd8+ Bf8 30. Nxe1 Bd5 31. Nf3 Ne4 32. Qb8 b5 33. h4 h5 34. Ne5 Kg7 35. Kg1 Bc5+ 36. Kf1 Ng3+ 37. Ke1 Bb4+ 38. Kd1 Bb3+ 39. Kc1 Ne2+ 40. Kb1 Nc3+ 41. Kc1 Rc2# 0-1
                """;

        PGNReader reader = new PGNReader();

        assertDoesNotThrow(() -> reader.parseString(pgnString));

        assertEquals(82, reader.getMoves().size());

        assertEquals(SpecialMove.KING_SIDE_CASTLE, reader.getMoves().get(7).specialMove());

        assertTrue(reader.getMoves().get(12).isCapture());
    }
}