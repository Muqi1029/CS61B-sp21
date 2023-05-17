## 1. AList


### 1.1 Timing the construction of an AList with a bad resize strategy
           N     time (s)        # ops  microsec/op
------------------------------------------------------------
        1000         0.00         1000         1.00
        2000         0.00         2000         2.00
        4000         0.01         4000         3.25
        8000         0.06         8000         7.88
       16000         0.13        16000         8.06
       32000         0.47        32000        14.72
       64000         1.41        64000        22.06
      128000         4.85       128000        37.86

### 1.2  with a good resize strategy
           N     time (s)        # ops  microsec/op
------------------------------------------------------------
        1000         0.00         1000         1.00
        2000         0.00         2000         0.00
        4000         0.00         4000         0.50
        8000         0.00         8000         0.25
       16000         0.00        16000         0.25
       32000         0.01        32000         0.22
       64000         0.01        64000         0.11
      128000         0.01       128000         0.11


## 2. SLList
Suppose we want to compute the time per operation for getLast for an SLList and want to know how this runtime depends on N. To do this, we need to follow the procedure below:

1. Create an SLList.
2. Add N items to the SLList.
3. Start the timer.
4. Perform M getLast operations on the SLList.
5. Check the timer. This gives the total time to complete all M operations.


           N     time (s)        # ops  microsec/op
------------------------------------------------------------
        1000         0.02        10000         1.50
        2000         0.03        10000         2.50
        4000         0.05        10000         5.10
        8000         0.10        10000        10.20
       16000         0.20        10000        20.40
       32000         0.41        10000        41.00
       64000         0.83        10000        82.90
      128000         1.70        10000       169.60