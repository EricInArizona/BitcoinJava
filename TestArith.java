// Copyright Eric Chauvin 2021.



public class TestArith
  {
  private MainApp mApp;
  private EPoint[] pointArray;
  private int pointArrayLast = 0;
  public SPrimes primes;



  public TestArith( MainApp useApp )
    {
    mApp = useApp;
    try
    {
    primes = new SPrimes();
    }
    catch( Exception e )
      {
      mApp.showStatusAsync(
          "Exception in TestArith constructor." );
      mApp.showStatusAsync( e.getMessage() );
      }
    }


  public void mainTest() throws Exception
    {
    try
    {
    // Primes have to not be 2 or 3.
    // The third prime is 5.
    for( int count = 3; count < 50; count++ )
      {
      long prime = primes.getPrimeAt( count );
      if( prime == EPoint.coefB )
        continue;

      mApp.showStatusAsync( "\n\nPrime is: " +
                                         prime );
      makeCurvePoints( prime );
      }
    }
    catch( Exception e )
      {
      mApp.showStatusAsync(
             "Exception in TestArith." );
      mApp.showStatusAsync( e.getMessage() );
      }
    }



  public void makeCurvePoints( long base )
                                throws Exception
    {
    // The curve used in Bitcoin: y^2 = x^3 + 7

    mApp.showStatusAsync( "Making curve points." );

    // The maximum number of points that can
    // be generated by these loops is:
    // (base * base ) + 1
    // But it's usually closer to base + 1.

    final int pointArraySize =
                            (int)((base * 2) + 1);
    pointArray = new EPoint[pointArraySize];
    pointArrayLast = 0;

    // Add only one point at Infinity.
    EPoint point = new EPoint();
    point.makeInfinite();
    pointArray[pointArrayLast] = point;
    pointArrayLast++;

    EPoint accumPoint = new EPoint();
    EPoint dPoint = new EPoint();

    // This is the definition of the phrase
    // "all of the points on a curve".
    // Everything that goes in to pointArray
    // is a point on the curve.
    // Notice that the point at 0,0 might or
    // might not be on the curve, if it works
    // with the equation.  If isOnCurve()
    // returns true.

    for( int x = 0; x < base; x++ )
      {
      long xPart = (x * x * x) + 7;
      xPart = xPart % base;

      for( int y = 0; y < base; y++ )
        {
        point = new EPoint( x, y );
        if( !point.isOnCurve( base ))
          continue;

        if( pointArrayLast >= pointArraySize )
          throw new Exception(
                    "pointArraySize too small." );

        pointArray[pointArrayLast] = point;
        pointArrayLast++;

        if( !accumPoint.isConjugate( base, point ))
          {
          // But it can have a different X value,
          // with y values that might or might not
          // add up to zero.
          // if( accumPoint.y !=
          //   ModNumber.negate( base, point.y ))

          accumPoint.add( base, accumPoint,
                                         point );
          }

        dPoint.copy( point );
        // testDouble( base, dPoint );
        testScalarMult( base, dPoint );

        if( !accumPoint.isOnCurve( base ))
          {
          throw new Exception(
                  "!accumPoint.isOnCurve(). x:" +
                    accumPoint.x + " y: " +
                    accumPoint.y );
          }

        mApp.showStatusAsync( "x: " + x +
                                    " y: " + y );

        mApp.showStatusAsync( "accumPoint.x: " +
            accumPoint.x + " y: " + accumPoint.y );

        mApp.showStatusAsync( "dPoint.x: " +
          dPoint.x + " y: " + dPoint.y );

        }
      }

    // The number of points that are actually
    // on the curve.
    mApp.showStatusAsync( "pointArrayLast: " +
                               pointArrayLast );
    }



  public void testDouble( long base,
                                  EPoint dPoint )
                                  throws Exception
    {
    long twoPower = 1; // Two to the 0.
    // twoPower = 2;      Two to the 1.

    for( int count = 0; count < 16; count++ )
      {
      EPoint pTest = new EPoint( dPoint );
      EPoint pTestByAdd = new EPoint( dPoint );

      mApp.showStatusAsync( "twoPower: " +
                                       twoPower );
      pTest.repeatDoubleP( base, twoPower,
                                      pTest );
      pTestByAdd.repeatDoublePByAdd( base,
                         twoPower, pTestByAdd );

      if( !pTest.isOnCurve( base ))
        {
        throw new Exception(
                "repeatDouble() not on curve." );
        }

      if( !pTestByAdd.isOnCurve( base ))
        {
        throw new Exception(
            "repeatDoubleByAdd() not on curve." );
        }

      if( !pTest.isEqual( pTestByAdd ))
        throw new Exception(
              "!pTest.isEqual( pTestByAdd )" );

      twoPower <<= 1;
      if( twoPower > 0x80 )
        return;

      }
    }



  public void testScalarMult( long base,
                                  EPoint point )
                                  throws Exception
    {
    EPoint pTest = new EPoint();
    EPoint pTestByAdd = new EPoint();

    for( int count = 0; count < 50; count++ )
      {
      // mApp.showStatusAsync(
                         // "Count K: " + count );
      pTest.copy( point );
      pTestByAdd.copy( point );

      pTest.scalarMult( base, count );
      pTestByAdd.scalarMultByAdd( base, count );

      if( !pTest.isOnCurve( base ))
        {
        throw new Exception(
                "scalarMult() not on curve." );
        }

      if( !pTestByAdd.isOnCurve( base ))
        {
        throw new Exception(
            "scalarMultByAdd() not on curve." );
        }

      if( !pTest.isEqual( pTestByAdd ))
        throw new Exception(
          "scalar !pTest.isEqual( pTestByAdd )" );

      }
    }


  }