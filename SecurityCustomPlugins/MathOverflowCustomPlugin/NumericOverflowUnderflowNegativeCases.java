package com.google.errorprone.bugpatterns.testdata;

public class NumericOverflowUnderflowNegativeCases {

	public float okFloatAdd(float a, float b) throws ArithmeticException
	{
		if (a > 0 ? a > Float.MAX_VALUE - b
				: a < Float.MIN_VALUE - b)
		{
				throw new ArithmeticException("Addition will exceed return value");
		}
		
		return a+b;
	}
	
	public int okIntegerAdd(int a, int b) throws ArithmeticException
	{
		if (a > 0 ? a > Integer.MAX_VALUE - b
				: a < Integer.MIN_VALUE - b)
		{
				throw new ArithmeticException("Addition will exceed return value");
		}
		
		return a+b;
	}
	
	public int okIntegerSub(int a, int b) throws ArithmeticException
	{
		if(a > 0 ? a < (Integer.MIN_VALUE + b)
				: a > (Integer.MAX_VALUE + b))
			throw new ArithmeticException("Subtraction will Underflow!");
		
		return a-b;
	}

}
