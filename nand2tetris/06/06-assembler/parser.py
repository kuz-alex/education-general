from enum import Enum, auto
from code import Code

class States(Enum):
    COMMENT = auto()
    MNEMONIC = auto()

class Parser:
    def _split(self, line):
        """ State-machine implementation for splitting the line's mnemonics into a list. """
        result = []
        divider = [";"]

        mnemonic = ""

        # Building an array from the mnemonic.
        for c in line:
            if c == "/":
                # Ignore comments
                break
            if c == "@":
                result.append(c)
            elif c in divider:
                result.append(mnemonic)
                mnemonic = ""
            else:
                mnemonic += c

        if mnemonic.strip() != "":
            result.append(mnemonic)

        return result

    def _parse_c_instruction(self, instruction):
        # Maybe split instruction into 3 parts.
        # Use a Code class
        code = Code()
        parsed = ""

        if (len(instruction) == 1):
            # Handle ALU instruction
            mnemonics = instruction[0].split("=")
            parsed += code.comp(mnemonics[1])
            parsed += code.dest(mnemonics[0])
            parsed += "000"
        else:
            parsed += code.comp(instruction[0])
            parsed += "000"
            parsed += code.jump(instruction[1])

        return parsed

    def parse_line(self, line, symbols):
        if (line == '\n'):
            return ""

        l = self._split(line)

        if (len(l) < 1):
            return ""

        if l[0] == "@":
            # A-instruction.
            if l[1].isnumeric() == True:
                return '0' + format(int(l[1]), '015b') + '\n'
            else:
                # Variable.
                variable = l[1]
                address = symbols.get_entry(variable)
                if address == None:
                    address = symbols.add_entry(variable)

                return '0' + format(int(address), '015b') + '\n'
        else:
            # C-instruction.
            return '111' + self._parse_c_instruction(l) + '\n'

