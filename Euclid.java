// Copyright Eric Chauvin 2021.



// Euclidian Algorithms


public class Euclid
  {

  // This is the standard Extended Euclidean
  // Algorithm.  You can find it in a standard
  // textbook or with an Internet search.
  public static long multInverse( long base,
                                  long x )
                                  throws Exception
    {
    // The multiplicative inverse of a number X
    // is Inv * X = 1 mod base.  But if X is zero
    // then Inv can be anything and it will never
    // equal 1.
    // This algorithm looks for the Greatest
    // Common Divisor of base and x.  If that
    // common divisor is not 1 then x divides
    // base or vice versa.

    ModNumber.verifyMoreThanZero( x );

    // U is the old part to keep.
    long u0 = 0;
    long u1 = 1;
    long u2 = base;
    // V is the new part.
    long v0 = 1;
    long v1 = 0;
    long v2 = x;
    long t0 = 0;
    long t1 = 0;
    long t2 = 0;

    // while( not forever if there's a problem )
    for( int count = 0; count < 10000; count++ )
      {
      if( u2 < 0 )
        throw( new Exception( "U2 was negative." ));

      if( v2 < 0 )
        throw( new Exception( "V2 was negative." ));

      long quotient = u2 / v2;
      long remainder = u2 % v2;
      // If it divides it evenly then there
      // is not any multiplicative inverse
      // because the numbers have a GCD that
      // is not one.
      if( remainder == 0 )
        {
        // The GCD is not 1.
        return 0;
        // throw( new Exception()
        }

      long temp1 = u0;
      long temp2 = v0;
      temp2 = temp2 * quotient;
      temp1 = temp1 - temp2;

      // When does temp1 become negative?

      t0 = temp1;
      temp1 = u1;
      temp2 = v1;
      temp2 = temp2 * quotient;
      temp1 = temp1 - temp2;
      t1 = temp1;
      temp1 = u2;
      temp2 = v2;
      temp2 = temp2 * quotient;
      temp1 = temp1 - temp2;
      t2 = temp1;
      u0 = v0;
      u1 = v1;
      u2 = v2;
      v0 = t0;
      v1 = t1;
      v2 = t2;
      if( remainder == 1 )
        break;

      }

    long multInverse = t0;
    if( multInverse < 0 )
      {
      multInverse += base;
      }

    long testForModInverse1 = multInverse;
    long testForModInverse2 = x;
    testForModInverse1 = testForModInverse1 *
                              testForModInverse2;
    testForModInverse1 = testForModInverse1 % base;

    // By the definition of Multiplicative inverse:
    if( testForModInverse1 != 1 )
      throw new Exception( "MultInverse is wrong." );

    return multInverse;
    }


  }
