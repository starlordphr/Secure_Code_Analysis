package com.google.errorprone.bugpatterns.testdata;

public class NumericOverflowUnderflowPositiveCases {

	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public float addFloatOverflow(float a, float b)
	{
		return a+b;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public float multiplyFloatOverflow(float a, float b)
	{
		return a*b;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public float plusPlusFloatOverflow(float a)
	{
		return a++;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public float plusEqualFloatOverflow(float a)
	{
		a += 1;
		return a;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public float multEqualFloatOverflow(float a)
	{
		a *= 1;
		return a;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public float negationFloatOverflow(float a)
	{
		return -a;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public int subIntegerUnderflow(int a, int b)
	{
		return a-b;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public int divideIntegerUnderflow(int a, int b)
	{
		return a/b;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public int minusMinusIntegerUnderflow(int a)
	{
		return a--;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public int minusEqualIntegerUnderflow(int a)
	{
		a -= 1;
		return a;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public int divideEqualIntegerUnderflow(int a)
	{
		a /= 1;
		return a;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public int negationIntegerUnderflow(int a)
	{
		return -a;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public int addOverflow(int a, int b)
	{
		return a+b;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public int multiplyOverflow(int a, int b)
	{
		return a*b;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public int plusPlusOverflow(int a)
	{
		return a++;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public int plusEqualOverflow(int a)
	{
		a += 1;
		return a;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public int multEqualOverflow(int a)
	{
		a *= 1;
		return a;
	}
	
	// BUG: Diagnostic contains: Result may overflow or underflow return value
	public int negationOverflow(int a)
	{
		return -a;
	}
	
}
