class SymbolTable:
    def __init__(self):
        self._variable_address = 16
        # Add pre-defined symbols.
        self._table = dict({
            "SP": 0,
            "LCL": 1,
            "ARG": 2,
            "THIS": 3,
            "THAT": 4,
            "R0": 0,
            "R1": 1,
            "R2": 2,
            "R3": 3,
            "R4": 4,
            "R5": 5,
            "R6": 6,
            "R7": 7,
            "R8": 8,
            "R9": 9,
            "R10": 10,
            "R11": 11,
            "R12": 12,
            "R13": 13,
            "R14": 14,
            "R15": 15,
            "SCREEN": 16384,
            "KBD": 24576
        })

    def get_entry(self, key):
        return self._table.get(key)

    def add_entry(self, key, address = None):
        if address != None:
            self._table[key] = address
        else:
            self._table[key] = self._variable_address
            self._variable_address += 1

        return self._table[key]

    def get_symbols(self):
        return self._table
