// Copyright Eric Chauvin 2021.



// For small primes.

public class SPrimes
  {
  private long[] pArray;
  private int pArrayLast = 0;
  private final static int pArrayLength = 1024 * 16;


  public SPrimes() throws Exception
    {
    pArray = new long[pArrayLength];
    makePrimeArray();
    }



  public long getBiggestPrime()
    {
    return pArray[pArrayLength - 1];
    }



  public long getPrimeAt( int index )
    {
    // This should be an unsigned index, but Java
    // doesn't have that.
    if( index >= pArrayLength )
      return 0;

    return pArray[index];
    }



  public long getFirstPrimeFactor( long toTest )
                                  throws Exception
    {
    if( toTest < 2 )
      return 0;

    if( toTest == 2 )
      return 2;

    if( toTest == 3 )
      return 3;

    final long max = findULSqrRoot( toTest );
    if( max == 0 )
      { 
       throw( new Exception(
         "Max was zero." ));
      }

    for( int count = 0; count < pArrayLength;
                                         count++ )
      {
      long testN = pArray[count];
      if( testN < 1 )
        return 0;

      if( (toTest % testN) == 0 )
        return testN;

      if( testN > max )
        return 0;

      }

    return 0;
    }



  private void makePrimeArray()
                                  throws Exception
    {
    pArray[0] = 2;
    pArray[1] = 3;
    pArray[2] = 5;
    pArray[3] = 7;
    pArray[4] = 11;
    pArray[5] = 13;
    pArray[6] = 17;
    pArray[7] = 19;
    pArray[8] = 23;

    int last = 9;
    for( int testN = 29; ; testN += 2 )
      {
      if( (testN % 3) == 0 )
        continue;

      // If it has no prime factors then add it.
      if( 0 == getFirstPrimeFactor( testN ))
        {
        pArray[last] = testN;
        last++;
        if( last >= pArrayLength )
          return;

        }
      }
    }


  public long findULSqrRoot( long toMatch )
                                 throws Exception

    {
    long oneBit = 0x80000000L; // 0x8000 0000
    long result = 0;
    for( int count = 0; count < 32; count++ )
      {
      long toTry = result | oneBit;
      if( (toTry * toTry) <= toMatch )
        result |= oneBit; // Then I want the bit.

      oneBit >>= 1;
      }

    if( result == 0 )
      {
      throw( new Exception(
         "FindULSqrRoot() Result was zero." ));
      }

    // Test:
    if( (result * result) > toMatch )
      throw( new Exception(
         "FindULSqrRoot() Result is too high." ));

    // This would overflow if Answer was
                                 //   0xFFFFFFFF.
    if( result < 0xFFFFFFFFL )
      {
      if( ((result + 1) * (result + 1)) <=
                                         toMatch )
        throw( new Exception(
          "FindULSqrRoot() Result is too low." ));

      }

    return result;
    }




  }
