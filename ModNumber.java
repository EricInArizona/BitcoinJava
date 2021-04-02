// Copyright Eric Chauvin 2021.



// Basic modular arithmetic.


// If you said that something is a base-10
// number or a base-16 (hexadecimal) number,
// that number system is based on multiples of
// that base.  Base-10 numbers are based on
// 10, 100, 1000... and so on.
// When I use the word 'base' in modular
// arithmetic, I mean like if something is
// equal to 3 mod 7, then 7 is the base number.
// See verifyInBaseRange().



public class ModNumber
  {

  public static void verifyInBaseRange(
                                   long base,
                                   long toCheck )
                                   throws Exception
    {
    if( toCheck < 0 )
      throw new Exception( "verifyInBaseRange() exception 1." );

    if( toCheck >= base )
      throw new Exception( "verifyInBaseRange() exception 2." );

    }


  public static void verifyMoreThanZero(
                                   long toCheck )
                                   throws Exception
    {
    if( toCheck <= 0 )
      throw new Exception( "verifyMoreThanZero()." );

    }



  public static long add( long base, long in,
                                     long toAdd )
                                  throws Exception
    {
    verifyInBaseRange( base, toAdd );
    verifyInBaseRange( base, in );

    long result = in + toAdd;
    result = result % base;
    return result;
    }


  public static long negate( long base, long in )
                               throws Exception
    {
    // If y was 1 and base was 5.
    // 1 + (-1) = 0;
    // y = -1 + 5 = 4.
    // 1 + 4 = 0 mod 5.
    // The definition of the word 'negate':
    // y + negate( y ) = 0.

    // If I was using a table (array) I could use
    // only symbols from within the range zero
    // to base - 1.  But I use negative numbers
    // and numbers that are more than or equal
    // to base in order to do the math.
    // Like this assignment: result = -in.
    // I have to go outside of the set of numbers
    // in order to find the right numbers that
    // are inside of the set.

    verifyInBaseRange( base, in );
    long result = -in;
    result += base;
    return result;
    }



  public static long subtract( long base, long in,
                               long toSub )
                               throws Exception
    {
    verifyInBaseRange( base, in );
    verifyInBaseRange( base, toSub );
    long result = in - toSub;
    if( result < 0 )
      result += base;

    result = result % base;
    return result;
    }



  public static long multiply( long base, long in,
                               long toMul )
                               throws Exception
    {
    verifyInBaseRange( base, in );
    verifyInBaseRange( base, toMul );
    long result = in * toMul;
    result = result % base;
    return result;
    }



  public static long square( long base, long in )
                               throws Exception
    {
    verifyInBaseRange( base, in );
    long result = in * in;
    result = result % base;
    return result;
    }



  public static long divide( long base,
                             long numerator,
                             long divisor )
                             throws Exception
    {
    verifyInBaseRange( base, divisor );
    verifyInBaseRange( base, numerator );
    verifyMoreThanZero( divisor );

    if( numerator == 0 )
      return 0;

    if( divisor == 1 )
      {
      // num / denom = result
      // 1 / 1 = 1
      // num * denom^-1 = result
      // num * 1 = num
      return numerator;
      }

    // The definition of multiplicative inverse:
    // soAndSo * multInverse == 1.
    // You can't have a multiplicative inverse
    // of zero.  0 * anything = 0.  So it can't
    // equal 1.

    // base should be a prime, so the GCD with
    // divisor is 1.

    // Get the multiplicative inverse.
    long inv = Euclid.multInverse( base, divisor );
    // Returns 0 if they had a GCD > 1.
    if( inv == 0 )
      {
      throw new Exception(
             "Inv == 0 in divide(). " +
                divisor + ", " + base );
      }

    verifyInBaseRange( base, inv );

    long result = numerator * inv;
    result = result % base;
    return result;
    }



  }
