/**
 * File: rsg.cc
 * ------------
 * Provides the implementation of the full RSG application, which
 * relies on the services of the built-in string, ifstream, vector,
 * and map classes as well as the custom Production and Definition
 * classes provided with the assignment.
 */
 
#include <map>
#include <vector>
#include <fstream>
#include "definition.h"
#include "production.h"
using namespace std;

/**
 * Takes a reference to a legitimate infile (one that's been set up
 * to layer over a file) and populates the grammar map with the
 * collection of definitions that are spelled out in the referenced
 * file.  The function is written under the assumption that the
 * referenced data file is really a grammar file that's properly
 * formatted.  You may assume that all grammars are in fact properly
 * formatted.
 *
 * @param infile a valid reference to a flat text file storing the grammar.
 * @param grammar a reference to the STL map, which maps nonterminal strings
 *                to their definitions.
 */

static void readGrammar(ifstream& infile, map<string, Definition>& grammar)
{
  while (true) {
    string uselessText;
    getline(infile, uselessText, '{');
    if (infile.eof()) return;  // true? we encountered EOF before we saw a '{': no more productions!
    infile.putback('{');
    Definition def(infile);
    grammar[def.getNonterminal()] = def;
  }
}

vector<string> generateSentence(map<string, Definition> grammar, string key)
{
    Production prod = grammar[key].getRandomProduction();
    vector<string> result;

    for (
        Production::iterator productionCurr = prod.begin();
        productionCurr != prod.end();
        ++productionCurr
    ) {
        if (grammar.count(*productionCurr) != 0) {
            vector<string> lines = generateSentence(grammar, *productionCurr);
            for (int i = 0; i < lines.size(); ++i) {
                result.push_back(lines[i]);
            }
        } else {
            result.push_back(*productionCurr);
        }
    }

    return result;
}

void print_lines(vector<string> lines)
{
    for (int i = 0; i < lines.size(); ++i) {
        cout << lines[i];

        if (
            i + 1 < lines.size() &&
            lines[i + 1][0] != ',' && lines[i + 1][0] != '.'
        )
            cout << " ";
    }
    cout << endl;
}

/**
 * Performs the rudimentary error checking needed to confirm that
 * the client provided a grammar file.  It then continues to
 * open the file, read the grammar into a map<string, Definition>,
 * and then print out the total number of Definitions that were read
 * in.  You're to update and decompose the main function to print
 * three randomly generated sentences, as illustrated by the sample
 * application.
 *
 * @param argc the number of tokens making up the command that invoked
 *             the RSG executable.  There must be at least two arguments,
 *             and only the first two are used.
 * @param argv the sequence of tokens making up the command, where each
 *             token is represented as a '\0'-terminated C string.
 */

int main(int argc, char *argv[])
{
  if (argc == 1) {
    cerr << "You need to specify the name of a grammar file." << endl;
    cerr << "Usage: rsg <path to grammar text file>" << endl;
    return 1; // non-zero return value means something bad happened 
  }
  
  ifstream grammarFile(argv[1]);
  if (grammarFile.fail()) {
    cerr << "Failed to open the file named \"" << argv[1] << "\".  Check to ensure the file exists. " << endl;
    return 2; // each bad thing has its own bad return value
  }
  
  // things are looking good...
  map<string, Definition> grammar;
  readGrammar(grammarFile, grammar);

  // We start with `<start>` and use recursion to fill the non-terminals with
  // terminals.
  cout << "Version #1: -----------------------" << endl;
  vector<string> lines = generateSentence(grammar, "<start>");
  print_lines(lines);
  cout << "Version #2: -----------------------" << endl;
  lines = generateSentence(grammar, "<start>");
  print_lines(lines);
  cout << "Version #3: -----------------------" << endl;
  lines = generateSentence(grammar, "<start>");
  print_lines(lines);
  /*
  // Iterate through the map to see what's inside.
  map<string, Definition>::iterator curr = grammar.begin();
  while (curr != grammar.end()) {
      // cout << "TEST: " << curr->second.getRandomProduction() << endl;

  cout << "Starting: " << endl;
  Production prod = curr->second.getRandomProduction();

  for (
      Production::iterator productionCurr = prod.begin();
      productionCurr != prod.end();
      ++productionCurr
  ) {
      cout << "PRODUCTION, " << curr->first << ": " << *productionCurr << endl;
  }
      ++curr;
  }
  */

  return 0;
}
