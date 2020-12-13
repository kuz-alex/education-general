#include <stdio.h>

#include "bool.h"
#include "thread_107.h"

#define SECOND 1000000
#define NUM_TAS 20
#define NUM_STUDENTS 115
#define NUM_MACHINES 40

static void Student();
static void TA(int index);
static void SetupSemaphores();
static void FreeSemaphores();

static int Examine(void);
static void ReadEmail(void);
static void Debug(void);
static void Rejoice(void);
static int RandomInteger(int low, int high);

struct studentQueue {
    Semaphore availableMachines;
    Semaphore studentTaken;
    Semaphore taAssignment;
    int numStudentsDone;
} studentQueue;

struct homeworkExamination {
    Semaphore available;
    Semaphore requested;
    Semaphore finished;
    int examinationsFromTA[NUM_TAS];
    int lastTAIndex;
} homeworkExamination;
 
void main(void) 
{ 
    int i; 
    InitThreadPackage(false); 
    SetupSemaphores();
    char name[64];

    for (i = 0; i < NUM_TAS; i++) {
        sprintf(&name, "TA %d", i);
        ThreadNew(name, TA, 1, i); 
    }

    for (i = 0; i < NUM_STUDENTS; i++) {
        sprintf(&name, "Student %d", i);
        ThreadNew(name, Student, 0); 
    }
    RunAllThreads(); 
} 
 
static void TA(int index)
{
    while (true) {
        SemaphoreWait(homeworkExamination.requested);

        if (studentQueue.numStudentsDone == NUM_STUDENTS) {
            printf("TA \"%s\" went home.\n", ThreadName());
            break;
        }

        SemaphoreWait(studentQueue.taAssignment);
        printf("\tTA \"%s\" takes a student.\n", ThreadName());
        homeworkExamination.lastTAIndex = index;
        SemaphoreSignal(studentQueue.studentTaken);

        homeworkExamination.examinationsFromTA[index] = Examine();
        SemaphoreSignal(homeworkExamination.finished);

        ReadEmail();
    }
}

static void Student()
{
    int bugsNumber, assignedTA;

    SemaphoreWait(studentQueue.availableMachines);
    printf("%s takes the machine.\n", ThreadName());

    while (true) {
        Debug();

        SemaphoreWait(homeworkExamination.available);
        SemaphoreSignal(homeworkExamination.requested);

        SemaphoreWait(studentQueue.studentTaken);
        assignedTA = homeworkExamination.lastTAIndex;
        printf("\tTA %d gets assigned to a student \"%s\"\n", assignedTA, ThreadName());
        SemaphoreSignal(studentQueue.taAssignment);

        SemaphoreWait(homeworkExamination.finished);
        bugsNumber = homeworkExamination.examinationsFromTA[assignedTA];
        printf("%s gets a result, he has %d bugs.\n", ThreadName(), bugsNumber);

        SemaphoreSignal(homeworkExamination.available);
        if (bugsNumber == 0) {
            Rejoice();
            break;
        }
        if (bugsNumber >= 5) {
            break;
        }
    }

    SemaphoreSignal(studentQueue.availableMachines);
    studentQueue.numStudentsDone++;
    printf("%s exits.\n", ThreadName());

    // When the last student is out, we signal TA's to go home.
    if (studentQueue.numStudentsDone == NUM_STUDENTS) {
        for (int i = 0; i < NUM_TAS; ++i) {
            // Waking up all the TA's.
            SemaphoreSignal(homeworkExamination.requested);
        }
    }
}

static void SetupSemaphores()
{
    studentQueue.availableMachines = SemaphoreNew("Available Machines", NUM_MACHINES);
    studentQueue.studentTaken = SemaphoreNew("Student taken", 0);
    studentQueue.taAssignment = SemaphoreNew("TA Assignment", 1);
    studentQueue.numStudentsDone = 0;

    homeworkExamination.available = SemaphoreNew("TA available", NUM_TAS);
    homeworkExamination.requested = SemaphoreNew("TA requested", 0);
    homeworkExamination.finished = SemaphoreNew("TA finished", 0);
    homeworkExamination.lastTAIndex = 0;
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

