import sys
from parser import Parser
from symbol_table import SymbolTable

class Assembler:
    def __init__(self, in_f, out_f):
        self._in_f = in_f
        self._out_f = out_f
        self._symbol_table = SymbolTable()

    def compile(self):
        parser = Parser()

        # First pass, adding loop labels to the symbol table.
        file_buff = ""
        line_count = 0

        for line in self._in_f:
            if line[0] == '\n' or line[0] == "/":
                pass
            elif line[0] == "(":
                # Loop label
                label = line[1:-2]
                self._symbol_table.add_entry(label, line_count)
            else:
                file_buff += line.lstrip()
                line_count += 1

        # Second pass, actual translation to machine language.
        for line in file_buff.split('\n'):
            parsed = parser.parse_line(line, self._symbol_table)
            print(parsed, end='', file=self._out_f);

        print("Symbol table: ", self._symbol_table.get_symbols())

if __name__ == '__main__':
    in_f = sys.argv[1] + ".asm"
    out_f = sys.argv[1] + ".hack"

    with open(in_f, 'r') as i, open(out_f, 'w') as o:
        assembler = Assembler(i, o)
        assembler.compile()
