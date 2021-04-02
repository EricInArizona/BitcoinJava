// Copyright Eric Chauvin 2021.



// This is for a small scale demonstration of
// the algorithms used.  The real cryptographic
// code is in the BitcoinCpp repository at:
// https://github.com/EricInArizona/BitcoinCpp

// If you haven't studied any Abstract Algebra
// yet, like Groups, Rings and Fields, now
// would be the time to do it.

// This is for the arithmetic for points on
// an elliptic curve.  Add, subtract, multiply
// and divide points on a curve.  If the points
// are on the curve, then doing these operations
// will result in another point on the curve.

// Group:
// Closure, Associativity, Identity, Inverse

// Closure: If the result of one of these
// operations makes a point that's not on the
// curve, then it would be an error.  A bug in
// the program.  So the function isOnCurve()
// tests that.

// See an explanation at:
// ericinarizona.github.io/BitcoinCryptoTech.htm

// Domain parameters: (p, a, b, G, n, h )

// Compressed Point representation.
// The prefix byte, like 0x02 or 0x03 and
// 32 bytes for the X value of the point.
// Of the two possible y values.
// If it's 02 then y is even.
// If it's 03 then y odd.
// 04 is the uncompressed form.

// The prime p:
// p = FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFF
//     FFFFFFFF FFFFFFFF FFFFFFFE FFFFFC2F
// p equals 2^256 - 2^32 - 2^9 - 2^8 - 2^7 -
//        2^6 - 2^4 - 1

// The curve E: y^2 = x^3 + ax + b
// a = 0
// b = 7
// G is the Generator point.
// If you have the equation of the curve, and
// the point X, you can find Y.  So you only
// need to define X in what is called the
// compressed format for the representation of
// a point.

// SEC1 Point Representation:
// https://tools.ietf.org/id/
            // draft-jivsov-ecc-compact-05.html
// The 04 means it is uncompressed.
// G = 04
// X =    79BE667E F9DCBBAC 55A06295 CE870B07
//        029BFCDB 2DCE28D9 59F2815B 16F81798

// Y =    483ADA77 26A3C465 5DA4FBFC 0E1108A8
//        FD17B448 A6855419 9C47D08F FB10D4B8

// The order n of G:
// The scalar value for point multiplication
// Q = kP has to be less than this.  In other
// words the private key k has to be less than
// this.
// With ECDSA digital signing, operations are
// mod this N.  Not mod P.

// n = FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFE
//     BAAEDCE6 AF48A03B BFD25E8C D0364141

// The function #E( Fp ) means the number of
// points on the curve over the prime p.
// The cofactor is #E( Fp ) / n.
// Since the cofactor is 1, the number of points
// on the curve is equal to the order n.

// Cofactor: h = 01




public class EPoint
  {
  // This number has to be outside of the base
  // range.  Bigger than anything that would be
  // used as the base.  See ModNumber.java for
  // verifyInBaseRange().
  private static final long Infinity =
                             0x7FFFFFFFFFFFFFFFL;
  // The curve used in Bitcoin: y^2 = x^3 + 7
  public static final long coefA = 0;
  public static final long coefB = 7;
  public long x = 0;
  public long y = Infinity;



  public EPoint()
    {
    // Creating an otherwise undefined point.
    makeInfinite();
    }



  public EPoint( long setX, long setY )
    {
    x = setX;
    y = setY;
    }


  public EPoint( EPoint in )
    {
    x = in.x;
    y = in.y;
    }



  public boolean isOnCurve( long base )
    {
    if( isInfinite())
      return true;

    // The curve used in Bitcoin: y^2 = x^3 + 7
    long left = y * y;
    left = left % base;

    // y^2 = x^3 + ax + b
    long right = x * x; // x^2
    right = right % base;
    right = right * x;  // Now x^3.
    right += coefB;
    right = right % base;

    if( left == right )
      return true;

    return false;
    }



  void makeInfinite()
    {
    // This defines the meaning of the phrase
    // 'A Point at Infinity'.

    x = 1; // x = Anything.
    y = Infinity;
    }



  public boolean isInfinite()
    {
    if( y == Infinity )
      return true;

    // if( x == anything )

    return false;
    }



  void copy( EPoint p )
    {
    // This might be copying itself, so
    // this.x = p.x, which is OK.

    x = p.x;
    y = p.y;
    }



  public boolean isEqual( EPoint in )
    {
    if( x != in.x )
      return false;

    if( y != in.y )
      return false;

    return true;
    }



  public boolean isConjugate( long base, EPoint p )
                                  throws Exception
    {
    if( x != p.x )
      return false;

    if( y != ModNumber.negate( base, p.y ))
      return false;

    return true;
    }



/*
  //   negate()
  void conjugate( long base, EPoint p )
                                  throws Exception
    {
    // This defines the meaning of the phrase
    // Additive Identity:  P + -P = 0;
    // A curve like y^2 = x^3 + ax + b is
    // symmetric around the X axis.  As the curve
    // crosses the X axis it goes directly
    // vertical, which would be an infinite
    // slope, so to speak.
    // The conjugate has the same x coordinate
    // so it's:

    x = p.x;

    // But it has a negated y coordinate.
    // So the point and its conjugate are on
    // a vertical line.

    // If y was 1 and base was 5.
    // y = -1 + 5 = 4.
    // P + -P = 0.
    // 1 + 4 = 0 mod 5.
    // y + negate( y ) = 0.

    y = ModNumber.negate( base, p.y );
    }
*/



  public void add( long base, EPoint p, EPoint q )
                                throws Exception
    {
    if( coefB == base )
      throw new Exception( "Base can't be coefB." );

    // q + Infinity = q.
    if( p.isInfinite())
      {
      // If q was infinite too, then it's like
      // zero + zero = zero.
      copy( q );
      return;
      }

    if( q.isInfinite() )
      {
      copy( p );
      return;
      }

    if( p.isEqual( q ))
      {
      doubleP( base, p );
      return;
      }

    if( p.x == q.x ) // But y's are different.
      {
      // If it's the same x then the y values
      // can only be the negated values for
      // each other.  Like 3^2 = 9
      // and -3^2 = 9.

      if( !p.isConjugate( base, q ))
        {
        throw new Exception(
         "This can't happen. !p.isConjugate( q )" );

        }

      // Then the two points are in a vertical
      // line, and the line points to something
      // At Infinity.
      makeInfinite();
      return;
      }

    // One of these points, either p or q, might
    // be this same object.  Like P = P + Q.
    long px = p.x;
    long py = p.y;
    long qx = q.x;
    long qy = q.y;

    // The X values can be different, but
    // the numerator, the difference in y values,
    // can equal 0.  So the slope can be zero.

    // p.y - q.y
    long numerator = ModNumber.subtract( base,
                                       py, qy );

    // if( numerator == 0 )
    //   Then that's fine.

    long denom = ModNumber.subtract( base,
                                       px, qx );

    // The x values are not equal, so denom can't
    // be zero, but it can be 1.
    long slope = numerator; // If it is 1.
    if( denom > 1 )
      {
      // Avoid calling divide() if numerator is 1
      // because it would just return the
      // numerator if it was 1.
      slope = ModNumber.divide( base, numerator,
                                         denom );
      }

    long slopeSqr = ModNumber.square( base, slope );

    // x = slopeSqr - p.x - q.x;
    x = ModNumber.subtract( base, slopeSqr, px );
    x = ModNumber.subtract( base, x, qx );

    long xDelta = ModNumber.subtract( base,
                                      px, x );

    // if( xDelta == 0 )
    //   Then that's OK.

    // y = (slope * xDelta) - p.y;
    y = ModNumber.multiply( base, slope, xDelta );
    y = ModNumber.subtract( base, y, py );
    }



  public void doubleP( long base, EPoint p )
                                throws Exception
    {
    // coefA and coefB are the coefficients in
    // this equation:
    // y^2 = x^3 + ax + b
    // For Bitcoin a = 0 and b = 7.
    // The curve used in Bitcoin: y^2 = x^3 + 7

    // This might be doubling itself, so
    // this.x would be the same as p.x.

    long px = p.x;
    long py = p.y;

    // Double a point:
    // P + P = 2P
    // The number 3 comes from the derivative,
    // or the slope of the line, of x^3, so it's
    // 3x^2.  coefB is a constant and so it's
    // not in the derivative.

    if( p.isInfinite())
      {
      // Infinite + Infinite = Infinite
      copy( p );
      return;
      }

    if( py == 0 )
      {
      // Then the tangent to the line at P points
      // straight up.
      makeInfinite();
      return;
      }

    // The derivative of x^3 + ax is
    //                  3x^2 + a
    // Multiplying by 3 here means you don't
    // want to use base == 3.
    // numerator = (3 * (p.x * p.x)) + coefA;
    long numerator = coefA; // Zero in Bitcoin.
    if( px > 0 )
      {
      numerator = ModNumber.square( base, px );
      numerator = ModNumber.multiply( base, 3,
                                     numerator );
      numerator = ModNumber.add( base,
                                numerator, coefA );
      }

    // p.y is not zero here.
    // Multiplying by 2 here means you don't
    // want to use a base == 2.
    long denom = ModNumber.multiply( base, 2, py );
    long slope = ModNumber.divide( base, numerator,
                                   denom );
    long slopeSqr = ModNumber.square( base, slope );

    // x = slopeSqr - (2 * p.x);
    long rightSide = ModNumber.multiply( base,
                                         2, px );
    x = ModNumber.subtract( base, slopeSqr,
                                      rightSide );

    // p.x minus the new x.
    // y = slope * (p.x - x) - p.y;
    long xPart = ModNumber.subtract( base, px, x );
    y = ModNumber.multiply( base, slope, xPart );
    y = ModNumber.subtract( base, y, py );
    }



  public void repeatDoubleP( long base,
                             long twoPower,
                             EPoint p )
                                throws Exception
    {
    if( twoPower == 0 )
      throw new Exception( "twoPower == 0" );

    // It might be copying itself here.
    copy( p );

    if( twoPower == 1 ) // 2^0 = 1.
      return; // It's 2^0 times P.

    // If twoPower was 2, then shifting it right
    // once would leave it at 1, which means
    // double it once.
    // If it was 4 then shifting it to 2 would
    // mean double it twice.
    twoPower >>= 1;

    // If point becomes infinite, or if y becomes
    // zero, then it stays infinite.
    for( int count = 0; count < 1000000; count++ )
      {
      doubleP( base, this );
      twoPower >>= 1;
      if( twoPower == 0 )
        return;

      }
    }




  public void repeatDoublePByAdd( long base,
                                  long twoPower,
                                  EPoint p )
                                throws Exception
    {
    EPoint originalP = new EPoint( p );

    if( twoPower == 0 )
      throw new Exception( "twoPower == 0" );

    // It might be copying itself here.
    // This adds something one time.
    copy( p );

    if( twoPower == 1 )
      return; // It's 2^0 times P = P.

    for( int count = 0; count < (twoPower - 1);
                                        count++ )
      {
      add( base, this, originalP );
      }
    }



  public void scalarMult( long base, long k )
                                throws Exception
    {
    // If k = 23
    // 23 = 16 + 7 = 10111.
    // 23 = 2^4 + 2^2 + 2 + 1
    // 23 times P = (2^4 + 2^2 + 2 + 1)P
    // 23 times P = 2^4P + 2^2P + 2P + P

    EPoint accumP = new EPoint();
    accumP.makeInfinite(); // Additive Identiry.
    EPoint doubleP = new EPoint( this );

    int oneBit = 1;
    //                          63 bits.
    for( int count = 0; count < 63; count++ )
      {
      if( (k & oneBit) != 0 )
        {
        doubleP.repeatDoubleP( base, oneBit, this );
        accumP.add( base, doubleP, accumP );
        }

      oneBit <<= 1;
      }

    copy( accumP );
    }



  public void scalarMultByAdd( long base, long k )
                                throws Exception
    {
    EPoint accumP = new EPoint();
    accumP.makeInfinite(); // Additive Identiry.

    for( int count = 0; count < k; count++ )
      {
      accumP.add( base, accumP, this );
      }

    copy( accumP );
    }



  }
