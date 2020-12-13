
void defineAndFillArray()
{
    int arr[100];
    int i;
    for (i = 0; i < 100; ++i) {
        arr[i] = i;
    }
    return;
}

void printArray()
{
    int arr[100];
    int i;
    for (i = 0; i < 100; ++i) {
        printf("%d ", i);
    }
    printf("\n");
    return;
}

int main()
{
    int i;
    int array[4];

    for (i = 0; i <= 4; ++i) {
        array[i] = 0;
    }

    defineAndFillArray();
    printArray();
    return 0;
}
