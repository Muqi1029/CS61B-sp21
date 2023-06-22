import sys

def draw_hexagon(s):
    draw_hexagon_helper(s - 1, s)

def draw_hexagon_helper(num_space, num_tile):

    print(" " * num_space + "*" * num_tile)
    
    if num_space != 0:
        draw_hexagon_helper(num_space - 1, num_tile + 2)

    print(" " * num_space + "*" * num_tile)



if __name__ == '__main__':
    draw_hexagon(int(sys.argv[1]))
