CC = gcc
CFLAGS = -g -Wall -std=gnu99 -Wpointer-arith
LDFLAGS = -g -lrssnews -Llib/linux

SPARSE_STRING_SRCS = 01_sparsestringarray.c vector.c
SPARSE_STRING_OBJS = $(SPARSE_STRING_SRCS:.c=.o)

default: 01_sparsestringarray

01_sparsestringarray: vector.c vector.h $(SPARSE_STRING_OBJS)
	$(CC) -o $@ $(SPARSE_STRING_OBJS) $(LDFLAGS)

MULTITABLE_SRCS = multitable_p3.c
MULTITABLE_OBJS = $(MULTITABLE_SRCS:.c=.o)

multitable_p3:  $(MULTITABLE_OBJS)
	$(CC) $(MULTITABLE_OBJS) -o $@ $(LDFLAGS)

