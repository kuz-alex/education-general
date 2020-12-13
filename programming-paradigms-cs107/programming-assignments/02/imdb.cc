using namespace std;
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <unistd.h>
#include "imdb.h"

const char *const imdb::kActorFileName = "actordata";
const char *const imdb::kMovieFileName = "moviedata";

imdb::imdb(const string& directory)
{
  const string actorFileName = directory + "/" + kActorFileName;
  const string movieFileName = directory + "/" + kMovieFileName;
  
  actorFile = acquireFileMap(actorFileName, actorInfo);
  movieFile = acquireFileMap(movieFileName, movieInfo);
}

bool imdb::good() const
{
  return !( (actorInfo.fd == -1) || 
	    (movieInfo.fd == -1) ); 
}

// Returns the length of the string in bytes.
int getStringFromLocation(string& str, char* startAddr) {
    int i = 0;
    for (; *(startAddr + i) != '\0'; ++i) {
        str += *(startAddr + i);
    }
    // Take into account a zero byte.
    i++;

    // If the string with zero byte size is odd, we manually add one zero bit,
    // to make the whole number of bytes even.
    if (i % 2 != 0) {
        i += 1;
    }

    return i;
}

// Returns the length of the `title + year` in bytes.
int getFilmFromLocation(film &m, char* startAddr) {
    string title;
    // We can't rely on the `getStringFromLocation` here, becuase it accounts
    // for the extra zero byte after the string. In case with film, the extra
    // null byte is not added even if the title length is odd.

    int titleLen = 0;
    for (; *(startAddr + titleLen) != '\0'; ++titleLen) {
        title += *(startAddr + titleLen);
    }
    // Account for the zero byte at the end of the string.
    ++titleLen;

    m.title = title;

    // Retrive the movie year. Year delta always goes straight after the movie
    // title, we don't create any extra padding between them.
    int yearDelta = *((char*) (startAddr + titleLen));
    m.year = 1900 + yearDelta;
    int deltaLen = 1;

    // If the total size of title and 1-byte year delta is odd, then an extra
    // zero byte stored after year delta byte.
    return (titleLen + deltaLen) % 2 != 0
        ? titleLen + deltaLen + 1
        : titleLen + deltaLen;
}

static int actorCompare(const void* pointersArrayAddr, const void* actorOffset) {
    void*** actorFile = (void***) pointersArrayAddr;

    // We're passing string with actor name as the second pointer in the array.
    string needle;
    needle = **((string**) (char*) pointersArrayAddr + 1);

    // Offset is passed form within `bsearch` function, it corresponds to a
    // middle element.
    int offset = *((int*) actorOffset);

    // Retrieve the actor name for the given offset from the database.
    string name;
    char *nameStartAddr = (**((char***) actorFile)) + offset;
    getStringFromLocation(name, nameStartAddr);

    return strcmp(needle.c_str(), name.c_str());
}

bool imdb::getCredits(const string& player, vector<film>& films) const {
    // First byte is the total number.
    int actorsTotal = *((int*) actorFile);

    const void *keyPointers[2];
    keyPointers[0] = &actorFile;
    keyPointers[1] = &player;

    void *result = bsearch(keyPointers, (void*) ((int*) actorFile + 1), actorsTotal,
            sizeof(int), actorCompare);

    if (result == NULL) {
        cout << "No actor found in the database" << endl;
        return false;
    }

    // Take the actor from the database.
    int playerOffset = *((int*) result);

    string playerName;
    char *actorStartAddr = (char*) actorFile + playerOffset;
    int nameLen = getStringFromLocation(playerName, actorStartAddr);

    // Read the total number of movies for the actor.
    short totalMovies = *((short*) (actorStartAddr + nameLen));

    int totalLen = (nameLen + 2) % 4 == 0
        ? nameLen + 2
        : nameLen + 2 + 2;

    // Get all the movies for the actor in the database.
    int *movieOffsetsStartAddr = (int*) (actorStartAddr + totalLen);
    for (int i = 0; i < totalMovies; ++i) {
        char* filmStartAddr = (char*) movieFile + *(movieOffsetsStartAddr + i);
        film movie;
        getFilmFromLocation(movie, filmStartAddr);
        films.push_back(movie);
    }

    return true;
}

static int filmCompare(const void* pointersArrayAddr, const void* elementOffset) {
    // We have a void pointer to a void pointer, we cannot dereference a void
    // pointer, we need to cast it into something, e.g `char*`. But we can't
    // dereference `char*` more than once, we need to cast it again.
    void*** movieFile = (void***) pointersArrayAddr;

    film needle = **((film**) (char*) pointersArrayAddr + 1);

    // Offset is passed form within `bsearch` function, it corresponds to a
    // middle element.
    int offset = *((int*) elementOffset);

    // Retrieve the movie title for the given offset from the database.
    film movie;
    char *movieStartAddr = (**((char***) movieFile)) + offset;
    getFilmFromLocation(movie, movieStartAddr);

    // Compare the needle against the suggested movie item.
    return strcmp(needle.title.c_str(), movie.title.c_str()) != 0
        ? strcmp(needle.title.c_str(), movie.title.c_str())
        : needle.year - movie.year;
}

bool imdb::getCast(const film& movie, vector<string>& players) const {
    // Find the movie in the database using the binary search.
    int moviesTotal = *(int*) movieFile;

    // Create an array of pointers to a different type (we need a pointer to a
    // `movieFile` and a pointer to `struct film`).
    const void* keyPointers[2];

    // We store only 1 bit addresses of the entities.
    keyPointers[0] = &movieFile;
    keyPointers[1] = &movie;

    void* result = bsearch(keyPointers, (void*) ((int*) movieFile + 1), moviesTotal,
            sizeof(int), filmCompare);

    if (result == NULL) {
        cout << "No results in the movie database." << endl;
        return false;
    }

    // `result` is an int pointer to the movie offset. I need to cast it to
    // `int` to get the movie offset.
    int offset = *((int*) result);

    char* movieStartAddr = (char*) movieFile + offset;
    film m;
    int filmLen = getFilmFromLocation(m, movieStartAddr);

    // Determine the number of actors for the movie, stored in the two byte short.
    short totalActors = *((short*) ((char*) movieStartAddr + filmLen));

    int totalLen = (filmLen + 2) % 4 == 0
        ? filmLen + 2
        : filmLen + 2 + 2;

    // Now starts int array where each integer is an offset for the actors in
    // the database.

    int* actorsOffsetsStart = (int*) ((char*) movieStartAddr + totalLen);

    for (int i = 0; i < totalActors; ++i) {
        string actorName;
        char* actorStartLocation = (char*) actorFile + *(actorsOffsetsStart + i);
        // Problem here.
        getStringFromLocation(actorName, actorStartLocation);
        players.push_back(actorName);
    }

    return true;
}

imdb::~imdb()
{
  releaseFileMap(actorInfo);
  releaseFileMap(movieInfo);
}

// ignore everything below... it's all UNIXy stuff in place to make a file look like
// an array of bytes in RAM.. 
const void *imdb::acquireFileMap(const string& fileName, struct fileInfo& info)
{
  struct stat stats;
  stat(fileName.c_str(), &stats);
  info.fileSize = stats.st_size;
  info.fd = open(fileName.c_str(), O_RDONLY);
  return info.fileMap = mmap(0, info.fileSize, PROT_READ, MAP_SHARED, info.fd, 0);
}

void imdb::releaseFileMap(struct fileInfo& info)
{
  if (info.fileMap != NULL) munmap((char *) info.fileMap, info.fileSize);
  if (info.fd != -1) close(info.fd);
}
