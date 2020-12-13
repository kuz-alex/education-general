#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include <ctype.h>
#include <expat.h>

#include "url.h"
#include "bool.h"
#include "urlconnection.h"
#include "streamtokenizer.h"
#include "html-utils.h"
#include "vector.h"
#include "hashset.h"
#include "thread_107.h"

#define NUM_TICKETS 35
#define NUM_SELLERS 4

static int numTickets = NUM_TICKETS;
static Semaphore ticketsLock;

static void RandomDelay(int atLeastMicrosecs, int atMostMicrosecs)
{
    long choice;
    int range = atMostMicrosecs - atLeastMicrosecs;
    PROTECT(choice = random());
    ThreadSleep(atLeastMicrosecs + choice % range);
}

static void SellTickets(void)
{
    bool done = false;
    int numSoldByThisThread = 0;

    while (!done) {
        RandomDelay(500000, 2000000);
        SemaphoreWait(ticketsLock); // Enter critical section
        if (numTickets == 0) {
            done = true;
        } else {
            --numTickets;
            ++numSoldByThisThread;
            printf("%s sold one (%d left)\n", ThreadName(), numTickets);
        }
        SemaphoreSignal(ticketsLock);
    }

    printf("%s noticed that all tickets are sold! (I sold %d myself) \n", ThreadName(), numSoldByThisThread);
}

int main(int argc, char **argv)
{
    int i;
    char name[32];
    bool verbose = (argc == 2 && (strcmp(argv[1], "-v") == 0));

    InitThreadPackage(verbose);

    ticketsLock = SemaphoreNew("Tickets Lock", 1);
    for (i = 0; i < NUM_SELLERS; ++i) {
        sprintf(name, "Seller #%d", i);
        ThreadNew(name, SellTickets, 0);
    }

    RunAllThreads();
    SemaphoreFree(ticketsLock);

    printf("All done!\n");

    return 0;
}

