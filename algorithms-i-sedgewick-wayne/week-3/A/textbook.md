# Chapter 2.1 Mergesort

## Exercises
[2.2.1]
    In the `merge` method, we don't want to create an aux array to merge two arrays
    into. Because that would be inefficient.

    Abstract in-place `merge()` method.

    A E Q S U Y E I N O S T

    aux = A E Q S U Y | E I N O S T , a = []
    aux =   E Q S U Y | E I N O S T , a = [A]
    aux =   E Q S U Y |   I N O S T , a = [A, E]
    aux =     Q S U Y |   I N O S T , a = [A, E, E]
    aux =     Q S U Y |     N O S T , a = [A, E, E, I]
    aux =     Q S U Y |       O S T , a = [A, E, E, I, N]
    aux =       S U Y |       O S T , a = [A, E, E, I, N, Q]
    aux =       S U Y |         S T , a = [A, E, E, I, N, Q, O]
    aux =         U Y |         S T , a = [A, E, E, I, N, Q, O, S]
    aux =         U Y |           T , a = [A, E, E, I, N, Q, O, S, S]
    aux =         U Y |             , a = [A, E, E, I, N, Q, O, S, S, T]
    aux =             |             , a = [A, E, E, I, N, Q, O, S, S, T, U, Y]
