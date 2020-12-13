```
#include <stdio.h>

#include "bool.h"
#include "thread_107.h"

#define NUM_TAS 3
#define NUM_STUDENTS 9
#define NUM_MACHINES 5

static void Student();
static void TA(int index);
static void SetupSemaphores();
static void FreeSemaphores();

struct studentQueue {
    Semaphore availableMachines;
    bool allStudentsDone;
} studentQueue;

struct homeworkExamination {
    Semaphore available;
    Semaphore requested;
    Semaphore finished;
    Semaphore taken;
    int lastAssignedTA; // student should know which TA's grades his work.
    int examinationsByTA[NUM_TAS];
} homeworkExamination;

int main(void)
{
    int i;
    InitThreadPackage(false);
    for (i = 0; i < NUM_TAS; ++i) {
        ThreadNew("TA", TA, 1, i);
    }
    for (i = 0; i < NUM_STUDENTS; ++i) {
        ThreadNew("Student", Student, 0);
    }
    RunAllThreads();

    return 0;
}

static void TA(int index);
{
    int studentIndex;
    while (true) {
        SemaphoreWait(homeworkExamination.requested);
        if (studentQueue.allStudentsDone) {
            printf("TA \"%s\" just went home.\n", ThreadName());
            break;
        }

        // Write the current TA's index.
        homeworkExamination.lastAssignedTA = index;

        SemaphoreSignal(studentQueue.taken);

        homeworkExamination.examinations[studentIndex] = Examine();
        SemaphoreSignal(homeworkExamination.finished);
        ReadEmail();
    }
}

static void Student()
{
    int bugsNumber;

    SemaphoreWait(studentQueue.availableMachines);
    printf("\"%s\" takes the available machine.\n", ThreadName());

    while (true) {
        Debug();

        SemaphoreWait(homeworkExamination.available);
        SemaphoreSignal(homeworkExamination.requested);

        SemaphoreWait(homeworkExamination.taken);


        // Read the TA's index.
        int TAindex = homeworkExamination.lastAssignedTA;
        SemaphoreSignal(homeworkExamination.studentTaken);
        

        SemaphoreWait(homeworkExamination.finished);
        SemaphoreSignal(homeworkExamination.available);

        bugsNumber = homeworkExamination.examinations[myPosition];
        if (bugsNumber == 0) {
            Rejoice();
            break;
        }
        if (bugsNumber >= 10) {
            break;
        }
    }

    SemaphoreSignal(studentQueue.availableMachines);
    if (myPosition + 1 == NUM_STUDENTS) {
        PROTECT(studentQueue.allStudentsDone = true;);
        for (int i = 0; i < NUM_TAS; ++i) {
            // Waking up all the TA's.
            SemaphoreSignal(homeworkExamination.requested);
        }
    }
}

static void SetupSemaphores()
{
    studentQueue.allStudentsDone = false;
    studentQueue.availableMachines = SemaphoreNew("Available machines", NUM_MACHINES);

    homeworkExamination.available = SemaphoreNew("Examination available", NUM_TAS);
    homeworkExamination.requested = SemaphoreNew("Examination requested", NUM_TAS);
    homeworkExamination.finished = SemaphoreNew("Examination finished", NUM_TAS);
    homeworkExamination.taken = SemaphoreNew("Student Taken by TA", 1);;
    homeworkExamination.lastAssignedTA = 0;
    homeworkExamination.examinationsByTA = {0};
}

// these simulation functions don't do anything, just "fake"
static int Examine(void);
static void ReadEmail(void);
static void Debug(void);
static void Rejoice(void);
```
