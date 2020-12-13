#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "bool.h"
#include "thread_107.h"

#define NUM_MANAGERS 4
#define NUM_CUSTOMERS 10
#define SECOND 1000000

static void Cashier(int lineIndex);
static void Clerk(Semaphore done);
static void Manager(int totalNeed);
static void Customer(int numToBuy, char *name);
static void SetupSemaphores(void);
static void FreeSemaphores(void);
static int RandomInteger(int low, int high);
static void MakeCone(void);
static bool InspectCone(void);
static void Checkout(int lineIndex, int linePosition);
static void Browse(void);

struct inspection {
    Semaphore available;
    Semaphore requested;
    Semaphore finished;
    bool passed;
} inspection;

struct line {
    Semaphore lock;
    int nextPlaceInLines[2];
    Semaphore customerLines[2];
    Semaphore customers[2][NUM_CUSTOMERS + 1];
    bool noCustomersLeft;
} line;

int main(int argc, char **argv)
{
    int i, numCones, totalCones = 0;
    char *customerNames[NUM_CUSTOMERS];

    InitThreadPackage(false);
    SetupSemaphores();
    for (i = 0; i < NUM_CUSTOMERS; ++i) {
        char name[32];
        sprintf(name, "Customer %d", i + 1);
        customerNames[i] = strdup(name);

        numCones = RandomInteger(1, 4);
        ThreadNew(customerNames[i], Customer, 2, numCones, customerNames[i]);
        totalCones += numCones;
    }
    printf("Total amout of cones: %d.\n", totalCones);
    ThreadNew("Cashier 1", Cashier, 1, 0);
    ThreadNew("Cashier 2", Cashier, 1, 1);

    ThreadNew("Manager", Manager, 1, totalCones);

    RunAllThreads();
    printf("All done!\n");
    for (int i = 0; i < NUM_CUSTOMERS; ++i)
        free(customerNames[i]);
    FreeSemaphores();
    return 0;
}

static void Manager(int totalNeeded)
{
    int numPerfect = 0, numInspection = 0;

    while (numPerfect < totalNeeded) {
        SemaphoreWait(inspection.requested);
        ++numInspection;
        inspection.passed = InspectCone();
        if (inspection.passed)
            ++numPerfect;
        SemaphoreSignal(inspection.finished);
    }

    printf("Inspection success rate %d%%\n", (100 * numPerfect) / numInspection);
}

static void Clerk(Semaphore done)
{
    bool passed = false;

    while (!passed) {
        MakeCone();
        SemaphoreWait(inspection.available);
        SemaphoreSignal(inspection.requested);
        SemaphoreWait(inspection.finished);
        passed = inspection.passed; SemaphoreSignal(inspection.available);
    }

    SemaphoreSignal(done);
}

static void Customer(int numConesWanted, char *name)
{
    int i, myPlace;
    // Construct a unique name for the semaphore.
    char sName[32];
    sprintf(sName, "Count of clerks done (for %s)", name);
    Semaphore clerksDone = SemaphoreNew(sName, 0);

    for (i = 0; i < numConesWanted; ++i)
        ThreadNew("Clerk", Clerk, 1, clerksDone);

    Browse();

    for (i = 0; i < numConesWanted; ++i)
        SemaphoreWait(clerksDone);

    SemaphoreFree(clerksDone);

    // Customer randomly selects the queue.
    int selectedLine = RandomInteger(0, 1);

    SemaphoreWait(line.lock); // protect global
    myPlace = ++line.nextPlaceInLines[selectedLine];
    printf("\t\t\tCustomer takes a place %d in line %d.\n", myPlace, selectedLine);
    SemaphoreSignal(line.lock);

    SemaphoreSignal(line.customerLines[selectedLine]); // Request a cashier in current line.
    // We need the unique id based on the customer's N in queue and queue number.
    SemaphoreWait(line.customers[selectedLine][myPlace]); // wait til checked through
    printf("%s done!\n", ThreadName());
}

static void Cashier(int lineIndex)
{
    while ((line.nextPlaceInLines[0] + line.nextPlaceInLines[1]) < NUM_CUSTOMERS) {
        SemaphoreWait(line.customerLines[lineIndex]);

        if (line.noCustomersLeft) {
            // Handle the exit case.
            printf("\t\t\tThread \"%s\" just exits the work since there are no customers left.\n", ThreadName());
            return;
        }

        int customerPlaceInLine = line.nextPlaceInLines[lineIndex];
        Checkout(lineIndex, customerPlaceInLine);
        SemaphoreSignal(line.customers[lineIndex][customerPlaceInLine]);
    }

    // Increment the number of customers, to indicate that all are done.
    SemaphoreWait(line.lock);
    line.noCustomersLeft = true;
    SemaphoreSignal(line.lock);

    // Signal to the cashiers in other lines: "no more customers".
    SemaphoreSignal(line.customerLines[lineIndex == 0 ? 1 : 0]);
}

static void SetupSemaphores(void)
{
    int i;
    inspection.requested = SemaphoreNew("Inspection Requested", 0);
    inspection.finished = SemaphoreNew("Inspection Finished", 0);
    inspection.available = SemaphoreNew("Manager Available", 1);
    inspection.passed = false;

    line.lock = SemaphoreNew("Line lock", 1);
    line.customerLines[0] = SemaphoreNew("1st Line", 0);
    line.customerLines[1] = SemaphoreNew("2nd Line", 0);
    line.nextPlaceInLines[0] = 0;
    line.nextPlaceInLines[1] = 0;
    for (i = 0; i < NUM_CUSTOMERS; i++) {
        char name[32];
        sprintf(name, "%d Customer in 1st line", i);
        line.customers[0][i] = SemaphoreNew(name, 0);
        sprintf(name, "%d Customer in 2nd line", i);
        line.customers[1][i] = SemaphoreNew(name, 0);
    }
}

static void FreeSemaphores(void)
{
    int i;
    SemaphoreFree(inspection.requested);
    SemaphoreFree(inspection.finished);
    SemaphoreFree(inspection.available);
    SemaphoreFree(line.lock);
    for (i = 0; i < NUM_CUSTOMERS; i++) {
        SemaphoreFree(line.customers[0][i]);
        SemaphoreFree(line.customers[1][i]);
    }
}

/* These are just fake functions to stand in for processing steps */
static void MakeCone(void)
{
    ThreadSleep(RandomInteger(0, 3 * SECOND));    // sleep random amount
    printf("\t%s making an ice cream cone.\n", ThreadName());
}

static bool InspectCone(void)
{
    bool passed = (RandomInteger(1, 2) == 1);
    printf("\t\t%s examining cone, did it pass? %c\n", ThreadName(),
            (passed ? 'Y':'N'));
    ThreadSleep(RandomInteger(0, .5*SECOND));    // sleep random amount
    return passed;
}

static void Checkout(int lineIndex, int linePosition)
{
    printf("\t\t\t%s checking out customer in line %d at position #%d.\n",
            ThreadName(), lineIndex, linePosition);
    ThreadSleep(RandomInteger(0, SECOND));    // sleep random amount
}

static void Browse(void)
{
    ThreadSleep(RandomInteger(0, 5*SECOND));    // sleep random amount
    printf("%s browsing.\n", ThreadName());
}

static int RandomInteger(int low, int high)
{
    extern long random();
    long choice;
    int range = high - low + 1;

    PROTECT(choice = random());
    return low + choice % range;
}

