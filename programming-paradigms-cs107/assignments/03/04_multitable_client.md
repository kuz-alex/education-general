# Problem 4 (result: 1/1).
```
static void inRangePrint(void *keyAddr, void *valueAddr, void *auxData)
{
    char *zipcode;
    char *city;
    char *low;
    char *high;

    // Zipcodes are stored as arrays of 6 characters.
    zipcode = (char *) keyAddr;

    // Cities are stored as pointers to strings.
    city = *(char **) valueAddr;

    low = *(char **) auxData;
    high = *(char **) auxData + 1;

    if ((strcmp(zipcode, low) >= 0) && (strcmp(zipcode, high) <= 0)) {
        printf("%5s: %s\n", zipcode, city);
    }
}

void ListRecordsInRange(multitable *zipCodes, char *low, char *high)
{
    char *endpoints[] = {low, high};
    MultiTableMap(zipCodes, inRangePrint, endpoints);
}
```
