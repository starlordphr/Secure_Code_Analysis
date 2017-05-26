
public class IntegerOverflowTest {
	
	public IntegerOverflowTest()
	{
		
	}
	
	public int addOverflow(int a, int b)
	{
		return a+b;
	}
	
	public int multiplyOverflow(int a, int b)
	{
		return a*b;
	}
	
	public int plusPlusOverflow(int a)
	{
		return a++;
	}
	
	public int plusEqualOverflow(int a)
	{
		a += 1;
		return a;
	}
	
	public int multEqualOverflow(int a)
	{
		a *= 1;
		return a;
	}
	
	public int negationOverflow(int a)
	{
		return -a;
	}
	
	public int okAdd(int a, int b) throws ArithmeticException
	{
		if (a > 0 ? a > Integer.MAX_VALUE - b
				: a < Integer.MIN_VALUE - b)
		{
				throw new ArithmeticException("Addition will exceed return value");
		}
		
		return a+b;
	}

}
