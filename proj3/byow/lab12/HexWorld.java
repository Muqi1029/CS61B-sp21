package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 96;
    private static final int HEIGHT = 128;
    private static final long SEED = 102129221;
    private static final Random RANDOM = new Random(SEED);

    private static class Position {
        int x;
        int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position move(int dx, int dy) {
            return new Position(this.x + dx, this.y + dy);
        }
    }

    /**
     * @param tiles the whole tile
     * @param p     the position
     */
    private static void addHexagon(TETile[][] tiles, Position p, int size, TETile tile) {
        drawHelper(tiles, p, size - 1, size, tile);
    }

    /**
     * draw helper
     */
    private static void drawHelper(TETile[][] tiles, Position p, int num_space, int num_tile, TETile tile) {
        Position p1 = p.move(num_space, 0);
        for (int i = p1.x; i < p1.x + num_tile; i++) {
            tiles[i][p1.y] = tile;
        }

        if (num_space != 0) {
            Position nextRow = p.move(0, -1);
            drawHelper(tiles, nextRow, num_space - 1, num_tile + 2, tile);
        }

        Position reflectPos = p1.move(0, -(2 * num_space + 1));
        for (int i = reflectPos.x; i < reflectPos.x + num_tile; i++) {
            tiles[i][reflectPos.y] = tile;
        }
    }

    /**
     * @param tiles the whole world
     * @param p     the starting position
     * @param size  the size of hexagon
     * @param num   the number of hexagons in a column
     */
    private static void drawHexagonColumn(TETile[][] tiles, Position p, int size, int num) {
        if (num == 0) {
            return;
        }
        addHexagon(tiles, p, size, randomTile());
        drawHexagonColumn(tiles, p.move(0, -2 * size), size, num - 1);
    }

    /**
     * build the whole Hexagon world
     *
     * @param tiles the tile Array
     */
    private static void buildWorld(TETile[][] tiles, int size, int tessSize) {

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }
        Position p = new Position(20, 80);

        drawHexagonColumn(tiles, p, size, tessSize);
        // draw the first half from right to left
        for (int i = 1; i < tessSize; i++) {
            p = getTopRightNeighbor(p, size);
            drawHexagonColumn(tiles, p, size, tessSize + i);
        }

        for (int i = tessSize - 2; i >= 0; i--){
            p = getBottomRightNeighbor(p, size);
            drawHexagonColumn(tiles, p, size, tessSize + i);
        }
    }

    private static Position getTopRightNeighbor(Position p, int size) {
        return p.move( 2 * size - 1, size);
    }

    private static Position getBottomRightNeighbor(Position p, int size) {
        return p.move(2 * size - 1, -size);
    }

    /**
     * @return a random tile
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0:
                return Tileset.WALL;
            case 1:
                return Tileset.FLOWER;
            case 2:
                return Tileset.MOUNTAIN;
            case 3:
                return Tileset.FLOOR;
            default:
                return Tileset.GRASS;
        }
    }

    public static void main(String[] args) {

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];

        buildWorld(world, 4, 3);

        ter.renderFrame(world);
    }
}
