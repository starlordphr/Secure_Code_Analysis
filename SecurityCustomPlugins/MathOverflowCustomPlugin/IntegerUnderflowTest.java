
public class IntegerUnderflowTest {
	
	public IntegerUnderflowTest()
	{
		
	}
	
	public int subUnderflow(int a, int b)
	{
		return a-b;
	}
	
	public int divideUnderflow(int a, int b)
	{
		return a/b;
	}
	
	public int minusMinusUnderflow(int a)
	{
		return a--;
	}
	
	public int minusEqualUnderflow(int a)
	{
		a -= 1;
		return a;
	}
	
	public int divideEqualUnderflow(int a)
	{
		a /= 1;
		return a;
	}
	
	public int negationUnderflow(int a)
	{
		return -a;
	}
	
	public int okSub(int a, int b) throws ArithmeticException
	{
		if(a > 0 ? a < (Integer.MIN_VALUE + b)
				: a > (Integer.MAX_VALUE + b))
			throw new ArithmeticException("Subtraction will Underflow!");
		
		return a-b;
	}

}
