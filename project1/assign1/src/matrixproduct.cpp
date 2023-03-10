#include <stdio.h>
#include <iostream>
#include <iomanip>
#include <time.h>
#include <cstdlib>
#include <papi.h>
#include <algorithm>

using namespace std;

#define SYSTEMTIME clock_t

int calcMin(int a, int b);

void OnMult(int m_ar, int m_br)
{

    SYSTEMTIME Time1, Time2;

    char st[100];
    double temp;
    int i, j, k;

    double *pha, *phb, *phc;

    int EventSet = PAPI_NULL;
    long long values[2];
    int ret;

    ret = PAPI_library_init(PAPI_VER_CURRENT);
    if (ret != PAPI_VER_CURRENT)
        std::cout << "FAIL" << endl;

    ret = PAPI_create_eventset(&EventSet);
    if (ret != PAPI_OK)
        cout << "ERROR: create eventset" << endl;

    ret = PAPI_add_event(EventSet, PAPI_L1_DCM);
    if (ret != PAPI_OK)
        cout << "ERROR: PAPI_L1_DCM" << endl;

    ret = PAPI_add_event(EventSet, PAPI_L2_DCM);
    if (ret != PAPI_OK)
        cout << "ERROR: PAPI_L2_DCM" << endl;

    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

    for (i = 0; i < m_ar; i++)
        for (j = 0; j < m_ar; j++)
            pha[i * m_ar + j] = (double)1.0;

    for (i = 0; i < m_br; i++)
        for (j = 0; j < m_br; j++)
            phb[i * m_br + j] = (double)(i + 1);

    Time1 = clock();

    ret = PAPI_start(EventSet);
    if (ret != PAPI_OK)
        cout << "ERROR: Start PAPI" << endl;
    for (i = 0; i < m_ar; i++)
    {
        for (j = 0; j < m_br; j++)
        {
            for (k = 0; k < m_ar; k++)
            {
                phc[i * m_ar + j] += pha[i * m_ar + k] * phb[k * m_br + j];
            }
        }
    }

    Time2 = clock();

    ret = PAPI_stop(EventSet, values);
    if (ret != PAPI_OK)
        cout << "ERROR: Stop PAPI" << endl;
    printf("L1 DCM: %lld \n", values[0]);
    printf("L2 DCM: %lld \n", values[1]);

    ret = PAPI_reset(EventSet);
    if (ret != PAPI_OK)
        std::cout << "FAIL reset" << endl;

    cout << m_ar << " " << (double)(Time2 - Time1) / CLOCKS_PER_SEC << endl;

    ret = PAPI_remove_event(EventSet, PAPI_L1_DCM);
    if (ret != PAPI_OK)
        std::cout << "FAIL remove event" << endl;

    ret = PAPI_remove_event(EventSet, PAPI_L2_DCM);
    if (ret != PAPI_OK)
        std::cout << "FAIL remove event" << endl;

    ret = PAPI_destroy_eventset(&EventSet);
    if (ret != PAPI_OK)
        std::cout << "FAIL destroy" << endl;

    free(pha);
    free(phb);
    free(phc);
}

void OnMultLine(int m_ar, int m_br)
{
    SYSTEMTIME Time1, Time2;

    char st[100];
    int i, j, k;

    double *pha, *phb, *phc;

    int EventSet = PAPI_NULL;
    long long values[2];
    int ret;

    ret = PAPI_library_init(PAPI_VER_CURRENT);
    if (ret != PAPI_VER_CURRENT)
        std::cout << "FAIL" << endl;

    ret = PAPI_create_eventset(&EventSet);
    if (ret != PAPI_OK)
        cout << "ERROR: create eventset" << endl;

    ret = PAPI_add_event(EventSet, PAPI_L1_DCM);
    if (ret != PAPI_OK)
        cout << "ERROR: PAPI_L1_DCM" << endl;

    ret = PAPI_add_event(EventSet, PAPI_L2_DCM);
    if (ret != PAPI_OK)
        cout << "ERROR: PAPI_L2_DCM" << endl;

    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

    for (i = 0; i < m_ar; i++)
        for (j = 0; j < m_ar; j++)
            pha[i * m_ar + j] = (double)1.0;

    for (i = 0; i < m_br; i++)
        for (j = 0; j < m_br; j++)
            phb[i * m_br + j] = (double)(i + 1);

    Time1 = clock();

    ret = PAPI_start(EventSet);
    if (ret != PAPI_OK)
        cout << "ERROR: Start PAPI" << endl;
    for (i = 0; i < m_ar; i++)
    {
        for (k = 0; k < m_br; k++)
        {
            for (j = 0; j < m_ar; j++)
            {
                phc[i * m_ar + j] += pha[i * m_ar + k] * phb[k * m_br + j];
            }
        }
    }

    Time2 = clock();

    ret = PAPI_stop(EventSet, values);
    if (ret != PAPI_OK)
        cout << "ERROR: Stop PAPI" << endl;
    printf("L1 DCM: %lld \n", values[0]);
    printf("L2 DCM: %lld \n", values[1]);

    ret = PAPI_reset(EventSet);
    if (ret != PAPI_OK)
        std::cout << "FAIL reset" << endl;

    cout << m_ar << " " << (double)(Time2 - Time1) / CLOCKS_PER_SEC << endl;

    ret = PAPI_remove_event(EventSet, PAPI_L1_DCM);
    if (ret != PAPI_OK)
        std::cout << "FAIL remove event" << endl;

    ret = PAPI_remove_event(EventSet, PAPI_L2_DCM);
    if (ret != PAPI_OK)
        std::cout << "FAIL remove event" << endl;

    ret = PAPI_destroy_eventset(&EventSet);
    if (ret != PAPI_OK)
        std::cout << "FAIL destroy" << endl;

    free(pha);
    free(phb);
    free(phc);
}

void OnMultBlock(int m_ar, int m_br, int bkSize)
{
    SYSTEMTIME Time1, Time2;

    int subSize = m_ar / bkSize;
    char st[100];
    int i, j, k;

    double *pha, *phb, *phc;

    int EventSet = PAPI_NULL;
    long long values[2];
    int ret;

    ret = PAPI_library_init(PAPI_VER_CURRENT);
    if (ret != PAPI_VER_CURRENT)
        std::cout << "FAIL" << endl;

    ret = PAPI_create_eventset(&EventSet);
    if (ret != PAPI_OK)
        cout << "ERROR: create eventset" << endl;

    ret = PAPI_add_event(EventSet, PAPI_L1_DCM);
    if (ret != PAPI_OK)
        cout << "ERROR: PAPI_L1_DCM" << endl;

    ret = PAPI_add_event(EventSet, PAPI_L2_DCM);
    if (ret != PAPI_OK)
        cout << "ERROR: PAPI_L2_DCM" << endl;

    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

    for (i = 0; i < m_ar; i++)
        for (j = 0; j < m_ar; j++)
            pha[i * m_ar + j] = (double)1.0;

    for (i = 0; i < m_br; i++)
        for (j = 0; j < m_br; j++)
            phb[i * m_br + j] = (double)(i + 1);

    Time1 = clock();

    ret = PAPI_start(EventSet);
    if (ret != PAPI_OK)
        cout << "ERROR: Start PAPI" << endl;
    for (i = 0; i < m_ar; i += subSize)
    {
        for (k = 0; k < m_br; k += subSize)
        {
            for (j = 0; j < m_ar; j += subSize)
            {
                for (int a = i; a < calcMin(i, subSize); a++)
                {
                    for (int b = k; b < calcMin(k, subSize); b++)
                    {
                        for (int c = j; c < calcMin(j, subSize); c++)
                        {
                            phc[a * m_ar + c] += pha[a * m_ar + b] * phb[b * m_br + c];
                        }
                    }
                }
            }
        }
    }

    /*
    for (int jj = 0; jj < m_br; jj += subSize)
    {
        for (int kk = 0; kk < m_br; kk += subSize)
        {
            for (int i = 0; i < m_br; i++)
            {
                for (int j = jj; j < ((jj + subSize) > m_br ? m_br : (jj + subSize)); j++)
                {
                    for (int k = kk; k < ((kk + subSize) > m_br ? m_br : (kk + subSize)); k++)
                    {
                        phc[i * m_ar + j] += pha[i * m_ar + k] * phb[k * m_br + j];
                    }
                }
            }
        }
    }
    */

    Time2 = clock();

    ret = PAPI_stop(EventSet, values);
    if (ret != PAPI_OK)
        cout << "ERROR: Stop PAPI" << endl;
    printf("L1 DCM: %lld \n", values[0]);
    printf("L2 DCM: %lld \n", values[1]);

    ret = PAPI_reset(EventSet);
    if (ret != PAPI_OK)
        std::cout << "FAIL reset" << endl;

    cout << m_ar << " " << bkSize << " " << (double)(Time2 - Time1) / CLOCKS_PER_SEC << endl;

    ret = PAPI_remove_event(EventSet, PAPI_L1_DCM);
    if (ret != PAPI_OK)
        std::cout << "FAIL remove event" << endl;

    ret = PAPI_remove_event(EventSet, PAPI_L2_DCM);
    if (ret != PAPI_OK)
        std::cout << "FAIL remove event" << endl;

    ret = PAPI_destroy_eventset(&EventSet);
    if (ret != PAPI_OK)
        std::cout << "FAIL destroy" << endl;

    free(pha);
    free(phb);
    free(phc);
}

int calcMin(int a, int b)
{
    int min;

    min = a + b;

    return min;
}

void handle_error(int retval)
{
    printf("PAPI error %d: %s\n", retval, PAPI_strerror(retval));
    exit(1);
}

void init_papi()
{
    int retval = PAPI_library_init(PAPI_VER_CURRENT);
    if (retval != PAPI_VER_CURRENT && retval < 0)
    {
        printf("PAPI library version mismatch!\n");
        exit(1);
    }
    if (retval < 0)
        handle_error(retval);

    std::cout << "PAPI Version Number: MAJOR: " << PAPI_VERSION_MAJOR(retval)
              << " MINOR: " << PAPI_VERSION_MINOR(retval)
              << " REVISION: " << PAPI_VERSION_REVISION(retval) << "\n";
}

void autoSize()
{

    int choice = 1;
    cout << "1. Normal and one line multuplication test." << endl;
    cout << "2. Block Multiplication test" << endl;
    cout << "   Any other choice to leave" << endl;
    cin >> choice;

    if (choice == 1)
    {

        cout << "Normal multiplication: " << endl;

        for (int i = 600; i <= 3000; i += 400)
        {

            OnMult(i, i);
        }

        cout << endl;
        cout << "Line multiplication: " << endl;
        for (int i = 600; i <= 3000; i += 400)
        {
            OnMultLine(i, i);
        }

        for (int i = 4096; i <= 10240; i += 2048)
        {
            OnMultLine(i, i);
        }

        cout << endl;
    }
    else if (choice == 2)
    {

        cout << "Block multiplication: " << endl;
        for (int i = 4096; i <= 10240; i += 2048)
        {
            for (int j = 128; j <= 1024; j = j * 2)
            {
                OnMultBlock(i, i, j);
            }
        }
    }
}

int main(int argc, char *argv[])
{

    char c;
    int lin, col, blockSize;
    int op;

    int EventSet = PAPI_NULL;
    long long values[2];
    int ret;

    bool papi_use = false;

    if (papi_use)
    {

        ret = PAPI_library_init(PAPI_VER_CURRENT);
        if (ret != PAPI_VER_CURRENT)
            std::cout << "FAIL" << endl;

        ret = PAPI_create_eventset(&EventSet);
        if (ret != PAPI_OK)
            cout << "ERROR: create eventset" << endl;

        ret = PAPI_add_event(EventSet, PAPI_L1_DCM);
        if (ret != PAPI_OK)
            cout << "ERROR: PAPI_L1_DCM" << endl;

        ret = PAPI_add_event(EventSet, PAPI_L2_DCM);
        if (ret != PAPI_OK)
            cout << "ERROR: PAPI_L2_DCM" << endl;
    }
    op = 1;
    do
    {
        cout << endl
             << "1. Multiplication" << endl;
        cout << "2. Line Multiplication" << endl;
        cout << "3. Block Multiplication" << endl;
        cout << "4. Auto tests" << endl;
        cout << "0. Leave program." << endl;
        cout << "Selection?: ";
        cin >> op;
        if (op == 0)
            break;

        // Start counting
        if (papi_use)
        {
            ret = PAPI_start(EventSet);
            if (ret != PAPI_OK)
                cout << "ERROR: Start PAPI" << endl;
        }

        switch (op)
        {
            case 1:
                printf("Dimensions: lins=cols ? ");
                cin >> lin;
                col = lin;
                OnMult(lin, col);
                break;
            case 2:
                printf("Dimensions: lins=cols ? ");
                cin >> lin;
                col = lin;
                OnMultLine(lin, col);
                break;
            case 3:
                printf("Dimensions: lins=cols ? ");
                cin >> lin;
                col = lin;
                cout << "Block Size? ";
                cin >> blockSize;
                OnMultBlock(lin, col, blockSize);
                break;
            case 4:
                cout << "Auto test for diferent sizes" << endl;
                autoSize();
                break;
        }

        if (papi_use)
        {
            ret = PAPI_stop(EventSet, values);
            if (ret != PAPI_OK)
                cout << "ERROR: Stop PAPI" << endl;
            printf("L1 DCM: %lld \n", values[0]);
            printf("L2 DCM: %lld \n", values[1]);

            ret = PAPI_reset(EventSet);
            if (ret != PAPI_OK)
                std::cout << "FAIL reset" << endl;
        }
    } while (op != 0);

    if (papi_use)
    {

        ret = PAPI_remove_event(EventSet, PAPI_L1_DCM);
        if (ret != PAPI_OK)
            std::cout << "FAIL remove event" << endl;

        ret = PAPI_remove_event(EventSet, PAPI_L2_DCM);
        if (ret != PAPI_OK)
            std::cout << "FAIL remove event" << endl;

        ret = PAPI_destroy_eventset(&EventSet);
        if (ret != PAPI_OK)
            std::cout << "FAIL destroy" << endl;
    }
}