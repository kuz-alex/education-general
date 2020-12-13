#include <stdio.h>

#include "bool.h"
#include "thread_107.h"

#define SECOND 1000000
#define NUM_TAS 2
#define NUM_STUDENTS 10
#define NUM_MACHINES 5

static void Student();
static void TA(int index);
static void SetupSemaphores();
static void FreeSemaphores();

static int Examine(void);
static void ReadEmail(void);
static void Debug(void);
static void Rejoice(void);
static int RandomInteger(int low, int high);

struct ta {
    bool available;
    Semaphore availLock;
    Semaphore requested;
    Semaphore finished;
    int numBugs;
} tas[NUM_TAS];

static Semaphore numTAsAvailable;
static Semaphore numMachinesAvailable;
static int numStudentsLeft = NUM_STUDENTS;
static Semaphore studentLeftLock;

void main(void) 
{ 
    int i; 
    InitThreadPackage(false); 
    SetupSemaphores();
    char name[64];

    for (i = 0; i < NUM_TAS; i++) {
        sprintf(name, "TA %d", i);
        ThreadNew(name, TA, 1, i); 
    }

    for (i = 0; i < NUM_STUDENTS; i++) {
        sprintf(name, "Student %d", i);
        ThreadNew(name, Student, 0); 
    }
    RunAllThreads(); 
}
 
static void TA(int index)
{
    while (true) {
        SemaphoreWait(tas[index].requested);

        if (numStudentsLeft == 0) {
            printf("TA \"%s\" went home.\n", ThreadName());
            break;
        }

        tas[index].numBugs = Examine();
        SemaphoreSignal(tas[index].finished);
        ReadEmail();
    }
}

static void Student()
{
    int bugsNumber = 1;
    int assignedTA;

    SemaphoreWait(numMachinesAvailable);
    printf("%s takes the machine.\n", ThreadName());

    while (bugsNumber > 0 && bugsNumber < 6) {
        Debug();

        SemaphoreWait(numTAsAvailable);
        for (assignedTA = 0; assignedTA < NUM_TAS; ++assignedTA) {
            SemaphoreWait(tas[assignedTA].availLock);
            if (tas[assignedTA].available) break;
            SemaphoreSignal(tas[assignedTA].availLock);
        }

        tas[assignedTA].available = false;
        SemaphoreSignal(tas[assignedTA].availLock);
        SemaphoreSignal(tas[assignedTA].requested);
        SemaphoreWait(tas[assignedTA].finished);
        bugsNumber = tas[assignedTA].numBugs;

        tas[assignedTA].available = true;
        SemaphoreSignal(numTAsAvailable);
    }

    if (bugsNumber == 0) Rejoice();
    printf("%s exits.\n", ThreadName());

    SemaphoreWait(studentLeftLock);
    numStudentsLeft--;
    bool everyoneDone = numStudentsLeft == 0;
    SemaphoreSignal(studentLeftLock);

    // When the last student is out, we signal TA's to go home.
    if (everyoneDone) {
        for (int i = 0; i < NUM_TAS; ++i) {
            // Waking up all the TA's.
            SemaphoreSignal(tas[i].requested);
        }
    }

    SemaphoreSignal(numMachinesAvailable);
}

static void SetupSemaphores()
{
    for (int i = 0; i < NUM_TAS; ++i) {
        char name[64];
        tas[i].available = true;
        tas[i].numBugs = 0;

        sprintf(name, "%d avail", i);
        tas[i].availLock = SemaphoreNew(name, 1);

        sprintf(name, "%d requested", i);
        tas[i].requested = SemaphoreNew(name, 0);

        sprintf(name, "%d finished", i);
        tas[i].finished = SemaphoreNew(name, 0);
    }
    
    numTAsAvailable = SemaphoreNew("TAs Available", NUM_TAS);
    numMachinesAvailable = SemaphoreNew("Machines Available", NUM_MACHINES);
    studentLeftLock = SemaphoreNew("StudentsLeft lock", 1);
}

// these simulation functions don't do anything, just "fake"
static int Examine(void)
{
    bool numberOfBugs = RandomInteger(0, 10);
    printf("\t%s has examined the work, found %d bugs.\n", ThreadName(), numberOfBugs);
    ThreadSleep(RandomInteger(0, .5*SECOND));    // sleep random amount
    return numberOfBugs;
}

static void ReadEmail(void)
{
    printf("\t%s starts reading email.\n", ThreadName());
    ThreadSleep(RandomInteger(0, 5*SECOND));    // sleep random amount
};

static void Debug(void)
{
    printf("%s starts debugging.\n", ThreadName());
    ThreadSleep(RandomInteger(0, 5*SECOND));    // sleep random amount
}

static void Rejoice(void)
{
    printf("\t%s rejoiced!\n", ThreadName());
}

static int RandomInteger(int low, int high)
{
    extern long random();
    long choice;
    int range = high - low + 1;

    PROTECT(choice = random());
    return low + choice % range;
}

